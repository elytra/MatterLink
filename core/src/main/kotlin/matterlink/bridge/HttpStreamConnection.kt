package matterlink.bridge

import matterlink.config.cfg
import matterlink.instance
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.HttpClients
import java.io.InputStream
import java.net.SocketException
import java.util.concurrent.ConcurrentLinkedQueue

const val BUFFER_SIZE = 1000

/**
 * adds the correct headers for MatterBridge authorization
 */
fun HttpRequestBase.authorize() {
    if (cfg.connect.authToken.isNotEmpty() && getHeaders("Authorization").isEmpty())
        setHeader("Authorization", "Bearer " + cfg.connect.authToken)
}

class HttpStreamConnection(private val rcvQueue: ConcurrentLinkedQueue<ApiMessage>,
                           private val clear: Boolean = true
) : Thread() {
    var connected = false
    var connecting = false
    var enabled = true
    var connectErrors = 0

    init {
        name = "MsgRcvThread"
    }

    private fun onClose() {
        instance.warn("Bridge connection closed!")
        connected = false
        connecting = false
    }

    private fun setSuccess(success: Boolean) {
        connecting = false
        if (success) {
            instance.info("connected successfully")
            connectErrors = 0
            connected = true
        } else {
            connectErrors++
            connected = false
        }
    }

    private val client = HttpClients.createDefault()
    private var stream: InputStream? = null

    val get = HttpGet(cfg.connect.url + "/api/stream").apply {
        authorize()
    }
    private val clearGet = HttpGet(cfg.connect.url + "/api/messages").apply {
        authorize()
    }
    
    private var cancelled: Boolean = false

    override fun run() {

        if (clear) {
            val r = client.execute(clearGet)
            r.entity.content.bufferedReader().forEachLine {
                instance.debug("skipping $it")
            }
        }
        try {
            val response = client.execute(get)
            if (response.statusLine.statusCode != 200) {
                instance.error("Bridge Connection rejected... status code ${response.statusLine.statusCode}")
                setSuccess(false) //TODO: pass message
                onClose()
                return
            } else {
                instance.debug("Bridge Connection accepted")
                setSuccess(true) //TODO: pass message
            }

            val content = response.entity.content.buffered()
            stream = content
            var buffer = ""
            val buf = ByteArray(BUFFER_SIZE)
            instance.info("initialized buffer")
            while (!get.isAborted) {
                val chars = content.read(buf)
                if (chars > 0) {
                    buffer += String(buf.dropLast(buf.count() - chars).toByteArray())

                    instance.trace(buffer)

                    while (buffer.contains("\n")) {
                        val line = buffer.substringBefore("\n")
                        buffer = buffer.substringAfter("\n")

                        rcvQueue.add(
                                ApiMessage.decode(line)
                        )

                    }
                } else if (chars < 0) {
                    break
                }
            }

            instance.debug("closing stream")
            content.close()

        } catch (e: SocketException) {
            instance.info("error {}", e)
            if (!cancelled) {
                instance.error("Bridge Connection interrupted...")
                setSuccess(false)
            }
        } finally {
            instance.debug("thread finished")
            onClose()
        }
        return
    }

    fun open() {
        enabled = true
        if (!isAlive && cfg.connect.enable) {
            connecting = true
            super.start()
//            MessageHandler.transmit(ApiMessage(text="bridge connected", username="Server"))
        }
        if (isAlive) {
            instance.info("Bridge Connection opened")
        }
    }


    fun close() {
        instance.info("Closing bridge connection...")
//        MessageHandler.transmit(ApiMessage(text="bridge closing", username="Server"))
        try {
            enabled = false
            cancelled = true
            get.abort()
            join()
        } catch (e: SocketException) {
            instance.error("exception: $e")
        }
    }
}