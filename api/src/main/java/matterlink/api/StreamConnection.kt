package matterlink.api

import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.io.InputStream
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.Arrays
import java.util.LinkedList
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by nikky on 07/05/18.
 *
 * @author Nikky
 * @version 1.0
 */
class StreamConnection(private val rcvQueue: ConcurrentLinkedQueue<ApiMessage>) : Runnable {
    private var thread: Thread = createThread()
    private var urlConnection: HttpURLConnection? = null
    private val onSuccessCallbacks = LinkedList<(Boolean) -> Unit>()

    var logger = LogManager.getLogger("matterlink.api")
    var host = ""
    var token = ""

    var isConnected = false
        private set
    var isConnecting = false
        private set
    var isCancelled = false
        private set

    private fun createThread(): Thread {
        val thread = Thread(this)
        thread.name = "RcvThread"
        return thread
    }

    fun addOnSuccess(callback: (Boolean) -> Unit) {
        onSuccessCallbacks.add(callback)
    }

    fun removeOnSuccess(callback: (Boolean) -> Unit) {
        onSuccessCallbacks.remove(callback)
    }

    private fun onSuccess(success: Boolean) {
        isConnecting = false
        isConnected = success
        for (callback in onSuccessCallbacks) {
            callback(success)
        }
    }

    override fun run() {
        try {
            val serviceURL = "$host/api/stream"
            val myURL: URL

            myURL = URL(serviceURL)
            urlConnection = myURL.openConnection() as HttpURLConnection
            urlConnection!!.requestMethod = "GET"
            if (!token.isEmpty()) {
                val bearerAuth = "Bearer $token"
                urlConnection!!.setRequestProperty("Authorization", bearerAuth)
            }
            try {
                urlConnection!!.inputStream.use { input ->
                    logger.info("connection opened")
                    onSuccess(true)
                    //            BufferedInputStream bufferedInput = new BufferedInputStream(input, 8 * 1024);
                    val buffer = StringBuilder()
                    while (!isCancelled) {
                        val buf = ByteArray(1024)
                        Thread.sleep(10)
                        while (input.available() <= 0) {
                            if (isCancelled) break
                            Thread.sleep(10)
                        }
                        val chars = input.read(buf)

                        logger.trace( String.format("read %d chars", chars))
                        if (chars > 0) {
                            val added = String(Arrays.copyOfRange(buf, 0, chars))
                            logger.debug("DEBUG", "json: $added")
                            buffer.append(added)
                            while (buffer.toString().contains("\n")) {
                                val index = buffer.indexOf("\n")
                                val line = buffer.substring(0, index)
                                buffer.delete(0, index + 1)
                                rcvQueue.add(ApiMessage.decode(line))
                            }
                        } else if (chars < 0) {
                            break
                        }
                    }
                }
            } finally {
                onClose()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: ConnectException) {
            e.printStackTrace()
            onSuccess(false)
        } catch (e: IOException) {
            e.printStackTrace()
            onSuccess(false)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    private fun onClose() {
        logger.info("Bridge connection closed!")
        isConnected = false
        isConnecting = false
    }

    fun open() {
        if (!thread.isAlive) {
            thread = createThread()
            isConnecting = true
            isCancelled = false
            thread.start()
            logger.info("Starting Connection")
        }
        if (thread.isAlive) {
            logger.info("Bridge is connecting")
        }
    }

    fun close() {
        try {
            isCancelled = true
            if (urlConnection != null) {
                urlConnection!!.disconnect()
            }
            thread.join()
            logger.info("Thread stopped")
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
}
