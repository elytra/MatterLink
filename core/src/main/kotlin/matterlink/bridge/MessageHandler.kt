package matterlink.bridge

import matterlink.config.cfg
//import matterlink.logger
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

    private fun HttpRequestBase.authorize() {
        if (cfg!!.connect.authToken.isNotEmpty() && getHeaders("Authorization").isEmpty())
            setHeader("Authorization", "Bearer " + cfg!!.connect.authToken)
    }

    private fun createThread(clear: Boolean = true): HttpStreamConnection {
        println("Attempting to open bridge connection.")
        return HttpStreamConnection(
                {
                    HttpGet(cfg!!.connect.url + "/api/stream").apply {
                        authorize()
                    }
                },
                {
                    HttpGet(cfg!!.connect.url + "/api/messages").apply {
                        authorize()
                    }
                },
                {
                    rcvQueue.add(
                            ApiMessage.decode(it)
                    )
//                    println("Received: " + it)
                },
                {
                    println("Bridge connection closed!")
                    connected = false
                },
                clear
        )
    }

    fun transmit(msg: ApiMessage) {
        if (connected && streamConnection.isAlive) {
            println("Transmitting: " + msg)
            transmitMessage(msg)
        }
    }

    fun stop() {
        println("Closing bridge connection...")
//        MessageHandler.transmit(ApiMessage(text="bridge closing", username="Server"))
        streamConnection.close()
    }

    fun start(clear: Boolean = true): Boolean {
        if (!connected)
            streamConnection = createThread(clear)
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
                System.err.println("Server returned $code for $post")
            }
            sendErrors = 0
        } catch (e: IOException) {
            System.err.println("sending message caused $e")
            sendErrors++
            if (sendErrors > 5) {
                System.err.println("caught too many errors, closing bridge")
                stop()
            }
        }
    }
}

