package matterlink.bridge;

import matterlink.instance
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import java.io.InputStream
import java.net.SocketException

val BUFFER_SIZE = 1000

class HttpStreamConnection(getClosure: () -> HttpGet,
                           clearClosure: () -> HttpGet,
                           private val mhandler: (String) -> Unit,
                           private val onClose: () -> Unit,
                           private val setSuccess: (Boolean) -> Unit,
                           private val clear: Boolean = true
) : Thread() {
    private val client = HttpClients.createDefault()
    private var stream: InputStream? = null

    val get = getClosure()
    private val clearGet = clearClosure()
    var cancelled: Boolean = false
        private set

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
                        mhandler(line)
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

    fun close() {
        cancelled = true
        get.abort()
        join()

    }
}