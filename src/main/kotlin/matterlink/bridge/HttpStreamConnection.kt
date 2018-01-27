package matterlink.bridge;

import matterlink.MatterLink
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import java.io.InputStream
import java.net.SocketException

val BUFFER_SIZE = 1000

class HttpStreamConnection(private val getClosure: () -> HttpGet, private val mhandler: (String) -> Unit, private val onClose: () -> Unit) : Thread() {
    private val client = HttpClients.createDefault()
    private var stream: InputStream? = null

    val get = getClosure()
    var cancelled: Boolean = false
        private set


    override fun run() {
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

                    MatterLink.logger.debug(buffer)

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
//            MatterLink.logger.error("Bridge Connection interrupted...")
        }
        MatterLink.logger.debug("closing stream")
        content.close()
        MatterLink.logger.debug("thread finished")
        onClose()
        return
    }

    fun close() {
        cancelled = true
        get.abort()
        join()

    }
}