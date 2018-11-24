package matterlink.api

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.core.extensions.cUrlString
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import matterlink.Logger
import java.io.Reader
import java.net.ConnectException
import kotlin.coroutines.CoroutineContext

/**
 * Created by nikky on 07/05/18.
 *
 * @author Nikky
 * @version 1.0
 */
open class MessageHandler : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job()
    private var enabled = false

    private var connectErrors = 0
    private var reconnectCooldown = 0L
    private var sendErrors = 0

    private var sendChannel: SendChannel<ApiMessage> = senderActor()

    private val messageStream = Channel<ApiMessage>(Channel.UNLIMITED)
    @UseExperimental(ExperimentalCoroutinesApi::class)
    var broadcast: BroadcastChannel<ApiMessage> = broadcast {
        while (true) {
            val msg = messageStream.receive()
            send(msg)
        }
    }
        private set
    private val keepOpenManager = FuelManager().apply {
        timeoutInMillisecond = 0
        timeoutReadInMillisecond = 0
    }

    var config: Config = Config()

    var logger = object : Logger {
        override fun info(message: String) = println("INFO: $message")
        override fun debug(message: String) = println("DEBUG: $message")
        override fun error(message: String) = println("ERROR: $message")
        override fun warn(message: String) = println("WARN: $message")
        override fun trace(message: String) = println("TRACE: $message")
    }

    suspend fun stop(message: String? = null) {
        if (message != null && config.announceDisconnect) {
            sendStatusUpdate(message)
        }
        enabled = false
        rcvJob?.cancel()
        rcvJob = null
    }

    private var rcvJob: Job? = null

    suspend fun start(message: String?, clear: Boolean) {
        logger.debug("starting connection")
        if (clear) {
            clear()
        }

        enabled = true

        rcvJob = messageBroadcast()

        if (message != null && config.announceConnect) {
            sendStatusUpdate(message)
        }
    }

    private suspend fun clear() {
        val url = "${config.url}/api/messages"
        val (request, response, result) = url.httpGet()
            .apply {
                if (config.token.isNotEmpty()) {
                    headers["Authorization"] = "Bearer ${config.token}"
                }
            }
            .awaitStringResponseResult()

        when (result) {
            is Result.Success -> {
                val messages: List<ApiMessage> = JSON.nonstrict.parse(ApiMessage.list, result.value)
                messages.forEach { msg ->
                    logger.trace("skipping $msg")
                }
                logger.debug("skipped ${messages.count()} messages")
            }
            is Result.Failure -> {
                logger.error("failed to clear messages")
                logger.error("url: $url")
                logger.error("cUrl: ${request.cUrlString()}")
                logger.error("response: $response")
                logger.error(result.error.exception.localizedMessage)
                result.error.exception.printStackTrace()
            }
        }
    }

    open suspend fun sendStatusUpdate(message: String) {
        transmit(ApiMessage(text = message))
    }

    open suspend fun transmit(msg: ApiMessage) {
//        if (streamConnection.isConnected || streamConnection.isConnecting) {
        if (msg.username.isEmpty())
            msg.username = config.systemUser
        if (msg.gateway.isEmpty()) {
            logger.error("missing gateway on message: $msg")
            return
        }
        logger.debug("Transmitting: $msg")
        sendChannel.send(msg)
//        }
    }

    @Deprecated("use coroutine api", level = DeprecationLevel.ERROR)
    fun checkConnection() {
    }

    @UseExperimental(ObsoleteCoroutinesApi::class)
    private fun CoroutineScope.senderActor() = actor<ApiMessage>(context = Dispatchers.IO) {
        consumeEach {
            try {
                logger.debug("sending $it")
                val url = "${config.url}/api/message"
                val (request, response, result) = url.httpPost()
                    .apply {
                        if (config.token.isNotEmpty()) {
                            headers["Authorization"] = "Bearer ${config.token}"
                        }
                    }
                    .jsonBody(it.encode())
                    .responseString()
                when (result) {
                    is Result.Success -> {
                        logger.info("sent $it")
                        sendErrors = 0
                    }
                    is Result.Failure -> {
                        sendErrors++
                        logger.error("failed to deliver: $it")
                        logger.error("url: $url")
                        logger.error("cUrl: ${request.cUrlString()}")
                        logger.error("response: $response")
                        logger.error(result.error.exception.localizedMessage)
                        result.error.exception.printStackTrace()
//                    close()
                        throw result.error.exception
                    }
                }
            } catch (connectError: ConnectException) {
                connectError.printStackTrace()
                sendErrors++
            }
        }
    }

    private fun CoroutineScope.messageBroadcast() = launch(context = Dispatchers.IO + CoroutineName("msgBroadcaster")) {
        loop@ while (isActive) {
            logger.info("opening connection")
            val url = "${config.url}/api/stream"
            val (request, response, result) = keepOpenManager.request(Method.GET, url)
                .apply {
                    if (config.token.isNotEmpty()) {
                        headers["Authorization"] = "Bearer ${config.token}"
                    }
                }
                .responseObject(object : ResponseDeserializable<Unit> {
                    override fun deserialize(reader: Reader) =
                        runBlocking(Dispatchers.IO + CoroutineName("msgReceiver")) {
                            logger.info("connected successfully")
                            connectErrors = 0
                            reconnectCooldown = 0

                            reader.useLines { lines ->
                                lines.forEach { line ->
                                    val msg = ApiMessage.decode(line)
                                    logger.info("received: $msg")
                                    if (msg.event != "api_connect") {
                                        messageStream.send(msg)
                                    }
                                }
                            }
                        }
                })

            when (result) {
                is Result.Success -> {
                    logger.info("connection closed")
                }
                is Result.Failure -> {
                    connectErrors++
                    reconnectCooldown = connectErrors * 1000L
                    logger.error("connectErrors: $connectErrors")
                    logger.error("connection error")
                    logger.error("curl: ${request.cUrlString()}")
                    logger.error(result.error.localizedMessage)
                    result.error.exception.printStackTrace()
                    if (connectErrors >= 10) {
                        logger.error("Caught too many errors, closing bridge")
                        stop("Interrupting connection to matterbridge API due to accumulated connection errors")
                        break@loop
                    }
                }
            }
            delay(reconnectCooldown) // reconnect delay in ms
        }
    }
}
