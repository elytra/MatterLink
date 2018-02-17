package matterlink.bridge

import matterlink.config.cfg
import matterlink.instance
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import java.io.IOException
import java.net.SocketException
import java.util.concurrent.ConcurrentLinkedQueue


object MessageHandler {
    var connected = false
    private var connecting = false
    private var enabled = true
    private var connectErrors = 0
    private var sendErrors = 0
    private var streamConnection: HttpStreamConnection
    var rcvQueue = ConcurrentLinkedQueue<ApiMessage>()

    init {
        streamConnection = createThread()
        streamConnection.start()
        connected = true
    }

    private fun HttpRequestBase.authorize() {
        if (cfg.connect.authToken.isNotEmpty() && getHeaders("Authorization").isEmpty())
            setHeader("Authorization", "Bearer " + cfg.connect.authToken)
    }

    private fun createThread(clear: Boolean = true): HttpStreamConnection {
        instance.info("Attempting to open bridge connection.")
        return HttpStreamConnection(
                {
                    HttpGet(cfg.connect.url + "/api/stream").apply {
                        authorize()
                    }
                },
                {
                    HttpGet(cfg.connect.url + "/api/messages").apply {
                        authorize()
                    }
                },
                {
                    rcvQueue.add(
                            ApiMessage.decode(it)
                    )
//                    instance.debug("Received: " + it)
                },
                {
                    instance.warn("Bridge connection closed!")
                    connected = false
                    connecting = false
                },
                { success ->
                    connecting = false
                    if (success) {
                        instance.info("connected successfully")
                        connectErrors = 0
                        connected = true
                    } else {
                        connectErrors++
                        connected = false
                    }
                },
                clear
        )
    }

    fun transmit(msg: ApiMessage) {
        if ((connected || connecting) && streamConnection.isAlive) {
            instance.debug("Transmitting: " + msg)
            transmitMessage(msg)
        }
    }

    fun stop() {
        enabled = false
        instance.info("Closing bridge connection...")
//        MessageHandler.transmit(ApiMessage(text="bridge closing", username="Server"))
        try {
            streamConnection.close()
        } catch (e: SocketException) {
            instance.error("exception: $e")
        }
    }

    fun start(clear: Boolean = true) {
        enabled = true
        if (!connected)
            streamConnection = createThread(clear)
        if (!streamConnection.isAlive) {
            connecting = true
            streamConnection.start()

//            MessageHandler.transmit(ApiMessage(text="bridge connected", username="Server"))
        }
        if (streamConnection.isAlive) {
            instance.info("Bridge Connection opened")
        }

    }

    private fun transmitMessage(message: ApiMessage) {
        try {
            //open a connection
            val client = HttpClients.createDefault()
            val post = HttpPost(cfg.connect.url + "/api/message")
            val json = message.encode()
            instance.trace("Transmitting $json")
            post.entity = StringEntity(json, ContentType.APPLICATION_JSON)
            post.authorize()

            val response = client.execute(post)
            val code = response.statusLine.statusCode
            if (code != 200) {
                instance.error("Server returned $code for $post")
                sendErrors++
                if (sendErrors > 5) {
                    instance.error("caught too many errors, closing bridge")
                    stop()
                }
            }
            sendErrors = 0
        } catch (e: IOException) {
            instance.error("sending message caused $e")
            sendErrors++
            if (sendErrors > 5) {
                instance.error("caught too many errors, closing bridge")
                stop()
            }
        }
    }

    fun checkConnection(tick: Int) {
        if (enabled && tick % 20 == 0 && !MessageHandler.connected && !connecting) {

            if (connectErrors > 5) {
                instance.fatal("caught too many errors, closing bridge")
                stop()
                return
            }

            instance.info("Trying to reconnect")
            MessageHandler.start(clear = false)
        }
    }
}

