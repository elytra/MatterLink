package matterlink.api

import org.apache.logging.log4j.Logger
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by nikky on 07/05/18.
 *
 * @author Nikky
 * @version 1.0
 */
open class MessageHandler {
    private var enabled = false

    private var connectErrors = 0
    private var reconnectCooldown = 0
    private var sendErrors = 0

    var config: Config = Config()
        set(value) {
            field = value.apply {
                sync(streamConnection)
            }
        }

    //TODO: make callbacks: onConnect onDisconnect onError etc

    var queue: ConcurrentLinkedQueue<ApiMessage> = ConcurrentLinkedQueue()
        private set
    private var streamConnection: StreamConnection = StreamConnection(queue)

    var logger: Logger
    get() = streamConnection.logger
    set(l) {
        streamConnection.logger = l
    }


    private var nextCheck: Long = 0

    init {
        streamConnection.addOnSuccess { success ->
            if (success) {
                logger.info("connected successfully")
                connectErrors = 0
                reconnectCooldown = 0
            } else {
                reconnectCooldown = connectErrors
                connectErrors++
                logger.error(String.format("connectErrors: %d", connectErrors))
            }
        }
    }

    fun stop(message: String? = null) {
        if (message != null && config.announceDisconnect) {
            sendStatusUpdate(message)
        }
        enabled = false
        streamConnection.close()
    }


    fun start(message: String?, clear: Boolean) {
        config.sync(streamConnection)
        if (clear) {
            clear()
        }

        enabled = true
        streamConnection.open()

        if (message != null && config.announceConnect) {
            sendStatusUpdate(message)
        }
    }


    private fun clear() {
        try {
            val url = URL(config.url + "/api/messages")
            val conn = url.openConnection() as HttpURLConnection

            if (!config.token.isEmpty()) {
                val bearerAuth = "Bearer " + config.token
                conn.setRequestProperty("Authorization", bearerAuth)
            }

            conn.requestMethod = "GET"

            BufferedReader(InputStreamReader(conn.inputStream)).forEachLine { line ->
                logger.trace("skipping $line")
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    open fun sendStatusUpdate(message: String) {
        transmit(ApiMessage(text = message))
    }

    open fun transmit(msg: ApiMessage) {
        if (streamConnection.isConnected || streamConnection.isConnecting) {
            if (msg.username.isEmpty())
                msg.username = config.systemUser
            if (msg.gateway.isEmpty()) {
                logger.fatal("missing gateway on message: $msg")
                return
            }
            logger.debug("Transmitting: $msg")
            transmitMessage(msg)
        }
    }

    private fun transmitMessage(message: ApiMessage) {
        try {
            val url = URL(config.url + "/api/message")
            val conn = url.openConnection() as HttpURLConnection

            if (!config.token.isEmpty()) {
                val bearerAuth = "Bearer " + config.token
                conn.setRequestProperty("Authorization", bearerAuth)
            }

            val postData = message.encode()
            logger.trace(postData)

            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("charset", "utf-8")
            conn.setRequestProperty("Content-Length", "" + postData.toByteArray().size)
            conn.doOutput = true
            conn.doInput = true

            DataOutputStream(conn.outputStream).use { wr -> wr.write(postData.toByteArray()) }

            //            conn.getInputStream().close();
            conn.connect()
            val code = conn.responseCode
            if (code != 200) {
                logger.error("Server returned $code")
                sendErrors++
                if (sendErrors > 5) {
                    logger.error("Interrupting Connection to matterbridge API due to status code $code")
                    stop()
                }
            } else {
                sendErrors = 0
            }
        } catch (e: IOException) {
            e.printStackTrace()
            logger.error("sending message caused $e")
            sendErrors++
            if (sendErrors > 5) {
                logger.error("Caught too many errors, closing bridge")
                stop()
            }
        }

    }

    /**
     * clll this method every tick / cycle to make sure it is reconnecting
     */
    fun checkConnection() {
        if (enabled && !streamConnection.isConnected && !streamConnection.isConnecting) {
            logger.trace("check connection")
            logger.trace("next: $nextCheck")
            logger.trace("now: " + System.currentTimeMillis())
            if (nextCheck > System.currentTimeMillis()) return
            nextCheck = System.currentTimeMillis() + config.reconnectWait

            if (connectErrors >= 10) {
                logger.error("Caught too many errors, closing bridge")
                stop("Interrupting connection to matterbridge API due to accumulated connection errors")
                return
            }

            if (reconnectCooldown <= 0) {
                logger.info("Trying to reconnect")
                start("Reconnecting to matterbridge API after connection error", false)
            } else {
                reconnectCooldown--
            }
        }
    }
}
