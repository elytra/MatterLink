package matterlink.bridge

import matterlink.config.cfg
import matterlink.instance
import matterlink.stackTraceString
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.HttpClients
import java.io.InputStream
import java.net.SocketException
import java.net.UnknownHostException
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
                           private val clear: Boolean = true,
                           private val messageHandler: MessageHandler
) : Thread() {
    var connected = false
        private set

    var connecting = false
        private set

    var cancelled: Boolean = false
        private set

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
            messageHandler.connectErrors = 0
            connected = true
        } else {
            messageHandler.connectErrors++
            connected = false
            instance.warn("connectErrors: ${messageHandler.connectErrors}")
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

    override fun run() {
        try {
            instance.info("Attemping to open Bridge Connection")
            if (clear) {
                val r = client.execute(clearGet)

                r.entity.content.bufferedReader().forEachLine {
                    instance.debug("skipping $it")
                }
            }
            val response = client.execute(get)
            if (response.statusLine.statusCode != 200) {
                instance.error("Bridge Connection rejected... status code ${response.statusLine.statusCode}")
                setSuccess(false) //TODO: pass message
                onClose()
                when (response.statusLine.statusCode) {
                    400 -> {
                        instance.warn("Missing token, please use /bridge reload after entering correct information")
                        messageHandler.enabled = false
                    }
                    401 -> {
                        instance.warn("Incorrect token, please use /bridge reload after entering correct information")
                        messageHandler.enabled = false
                    }
                }
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
            instance.error(e.stackTraceString)
            if (!cancelled) {
                instance.error("Bridge Connection interrupted...")
                setSuccess(false)
            }
        } catch (e: UnknownHostException) {
            instance.error(e.message ?: e.stackTraceString)
//            instance.error(e.stackTraceString())
            setSuccess(false)
        } finally {
            instance.debug("thread finished")
            onClose()
        }
        return
    }

    fun open() {
        if (!isAlive) {
            connecting = true
            super.start()
//            MessageHandler.transmit(ApiMessage(text="bridge connected", username="Server"))
        }
        if (isAlive) {
            instance.info("Bridge is connecting")
        }
    }

    fun close() {
        instance.info("Closing bridge connection...")
//        MessageHandler.transmit(ApiMessage(text="bridge closing", username="Server"))
        try {
            cancelled = true
            get.abort()
            join()
        } catch (e: SocketException) {
            instance.error("exception: $e")
        }
    }
}