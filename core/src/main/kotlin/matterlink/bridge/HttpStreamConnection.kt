package matterlink.bridge;

import matterlink.instance
import matterlink.logger
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import java.io.InputStream
import java.net.SocketException

val BUFFER_SIZE = 1000

class HttpStreamConnection(getClosure: () -> HttpGet, clearClosure: () -> HttpGet, private val mhandler: (String) -> Unit, private val onClose: () -> Unit, private val clear: Boolean = true) : Thread() {
    private val client = HttpClients.createDefault()
    private var stream: InputStream? = null

    val get = getClosure()
    private val clearGet = clearClosure()
    var cancelled: Boolean = false
        private set


    override fun run() {
        instance.interrupted = false
        if (clear) {
            val r = client.execute(clearGet)
            r.entity.content.bufferedReader().forEachLine {
                logger.debug("skipping $it")
            }
        }
        val response = client.execute(get)
        val content = response.entity.content.buffered()
        stream = content
        //val reader = content.bufferedReader()
        var buffer = ""
        val buf = ByteArray(BUFFER_SIZE)
        try {
            while (!get.isAborted) {
                val chars = content.read(buf)
                if (chars > 0) {
                    buffer += String(buf.dropLast(buf.count() - chars).toByteArray())

                    logger.debug(buffer)

                    while (buffer.contains("\n")) {
                        val line = buffer.substringBefore("\n")
                        buffer = buffer.substringAfter("\n")
                        mhandler(line)
                    }
                } else if (chars < 0) {
                    break
                }
            }
        } catch (e: SocketException) {
            if (!cancelled) {
                logger.error("Bridge Connection interrupted...")
                instance.interrupted = true
                //TODO: mark connection as interrupted and try to reconnect
            }
        }
        logger.debug("closing stream")
        content.close()
        logger.debug("thread finished")
        onClose()
        return
    }

    fun close() {
        cancelled = true
        get.abort()
        join()

    }
}