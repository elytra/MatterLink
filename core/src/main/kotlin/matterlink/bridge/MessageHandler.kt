package matterlink.bridge

import matterlink.IMatterLink
import matterlink.cfg
import matterlink.logger
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import java.io.IOException
import java.util.concurrent.ConcurrentLinkedQueue


object MessageHandler {
    private var connected = false
    private var sendErrors = 0
    private var streamConnection: HttpStreamConnection
    var rcvQueue = ConcurrentLinkedQueue<ApiMessage>()

    init {
        streamConnection = createThread()
        streamConnection.start()
        connected = true
    }

    fun HttpRequestBase.authorize() {
        if (cfg!!.connect.authToken.isNotEmpty() && getHeaders("Authorization").isEmpty())
            setHeader("Authorization", "Bearer " + cfg!!.connect.authToken)
    }

    private fun createThread(): HttpStreamConnection {
        logger.info("Attempting to open bridge connection.")
        return HttpStreamConnection(
                {
                    HttpGet(cfg!!.connect.url + "/api/stream").apply {
                        authorize()
                    }
                },
                {
                    rcvQueue.add(
                            ApiMessage.decode(it)
                    )
                    logger.debug("Received: " + it)
                },
                {
                    logger.info("Bridge connection closed!")
                    connected = false
                }
        )
    }

    fun transmit(msg: ApiMessage) {
        if (connected && streamConnection.isAlive) {
            logger.debug("Transmitting: " + msg)
            transmitMessage(msg)
        }
    }

    fun stop() {
        logger.info("Closing bridge connection...")
//        MessageHandler.transmit(ApiMessage(text="bridge closing", username="Server"))
        streamConnection.close()
    }

    fun start(): Boolean {
        if (!connected)
            streamConnection = createThread()
        if (!streamConnection.isAlive) {
            streamConnection.start()
//            MessageHandler.transmit(ApiMessage(text="bridge connected", username="Server"))
            connected = true
            return connected
        }
        return connected
    }

    private fun transmitMessage(message: ApiMessage) {
        try {
            //open a connection
            val client = HttpClients.createDefault()
            val post = HttpPost(cfg!!.connect.url + "/api/message")

            post.entity = StringEntity(message.encode(), ContentType.APPLICATION_JSON)
            post.authorize()

            val response = client.execute(post)
            val code = response.statusLine.statusCode
            if (code != 200) {
                logger.error("Server returned $code for $post")
            }
            sendErrors = 0
        } catch (e: IOException) {
            logger.error("sending message caused $e")
            sendErrors++
            if (sendErrors > 5) {
                logger.error("caught too many errors, closing bridge")
                stop()
            }
        }
    }
}

