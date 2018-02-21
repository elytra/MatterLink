package matterlink.bridge

import matterlink.config.cfg
import matterlink.instance
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import java.io.IOException
import java.util.concurrent.ConcurrentLinkedQueue

object MessageHandler {
    var enabled: Boolean = false
    private var sendErrors = 0
    var connectErrors = 0
    var reconnectCoodown = 0
    private var streamConnection: HttpStreamConnection
    var rcvQueue = ConcurrentLinkedQueue<ApiMessage>()
        private set
    val connected get() = streamConnection.connected

    init {
        //initialized here so we can make sure rcvQueue is never null
        streamConnection = createThread()
    }

    private fun createThread(clear: Boolean = true): HttpStreamConnection {
        return HttpStreamConnection(
                rcvQueue,
                clear,
                this
        )
    }

    fun transmit(msg: ApiMessage) {
        if ((streamConnection.connected || streamConnection.connecting) && streamConnection.isAlive) {
            instance.debug("Transmitting: " + msg)
            transmitMessage(msg)
        }
    }

    fun stop(message: String?) {
        if (message != null && cfg.debug.announceDisconnect) {
            transmit(ApiMessage(
                    text = message
            ))
        }
        enabled = false
        streamConnection.close()
    }

    fun start(message: String?, clear: Boolean = true, firstRun: Boolean = false) {
        enabled = when {
            firstRun -> cfg.connect.autoConnect
            else -> true
        }

        if (!connected) {
            streamConnection = createThread(clear)
        }

        if (enabled) {
            streamConnection.open()
        }

        if (message != null && cfg.debug.announceConnect) {
            transmit(ApiMessage(
                    text = message //?: "Connected to matterbridge API"
            ))
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
                    instance.error("Caught too many errors, closing bridge")
                    stop("Interrupting Connection to matterbridge API due status code $code")
                }
            }
            sendErrors = 0
        } catch (e: IOException) {
            instance.error("sending message caused $e")
            sendErrors++
            if (sendErrors > 5) {
                instance.error("Caught too many errors, closing bridge")
                stop("Interrupting connection to matterbridge API, too many errors trying to send message")
            }
        }
    }

    fun checkConnection() {
        if (enabled && !streamConnection.connected && !streamConnection.connecting) {

            if (connectErrors >= 10) {
                instance.fatal("Caught too many errors, closing bridge")
                stop("Interrupting connection to matterbridge API due to accumulated connection errors")
                return
            }

            if (reconnectCoodown <= 0) {
                instance.info("Trying to reconnect")
                MessageHandler.start(clear = false, message = "Reconnecting to matterbridge API after connection error")
            } else {
                reconnectCoodown--
            }
        }
    }
}

