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
    private var sendErrors = 0
    private var streamConnection: HttpStreamConnection
    var rcvQueue = ConcurrentLinkedQueue<ApiMessage>()
        private set

    init {
        //initialized here so we can make sure rcvQueue is never null
        streamConnection = createThread()
    }

    val connected get() = streamConnection.connected

    private fun createThread(clear: Boolean = true): HttpStreamConnection {
        instance.info("Attempting to open bridge connection.")
        instance.info("queue: $rcvQueue")
        return HttpStreamConnection(
                rcvQueue,
                clear
        )
    }

    fun transmit(msg: ApiMessage) {
        if ((streamConnection.connected || streamConnection.connecting) && streamConnection.isAlive) {
            instance.debug("Transmitting: " + msg)
            transmitMessage(msg)
        }
    }

    fun stop() = streamConnection.close()

    fun start(clear: Boolean = true) {
        if (!connected)
            streamConnection = createThread(clear)
        streamConnection.open()
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
                    stop()
                }
            }
            sendErrors = 0
        } catch (e: IOException) {
            instance.error("sending message caused $e")
            sendErrors++
            if (sendErrors > 5) {
                instance.error("Caught too many errors, closing bridge")
                stop()
            }
        }
    }

    fun checkConnection(tick: Int) {
        if (streamConnection.enabled && tick % 20 == 0 && !streamConnection.connected && !streamConnection.connecting) {

            if (streamConnection.connectErrors > 5) {
                instance.fatal("Caught too many errors, closing bridge")
                stop()
                return
            }

            instance.info("Trying to reconnect")
            MessageHandler.start(clear = false)
        }
    }
}

