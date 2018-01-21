package civilengineering.bridge

import java.io.InputStreamReader
import java.net.HttpURLConnection

/**
 * Created by nikky on 20/01/18.
 * @author Nikky
 * @version 1.0
 */

class CancellableConnectionFollowThread (httpConnClosure: () -> HttpURLConnection, private val mhandler: (String) -> Unit): Thread() {
    val cancelGuard = Object()
    var waitingOnNetwork = true
    var cancelled = false
    val httpConn = httpConnClosure()

    override fun run() {
        try {
            httpConn.allowUserInteraction = false
            httpConn.instanceFollowRedirects = true
            httpConn.requestMethod = "GET"

            InputStreamReader(httpConn.inputStream).useLines {
                it.forEach{
                    synchronized(cancelGuard) {
                        if (cancelled) return
                        waitingOnNetwork = false
                    }
                    mhandler(it)
                    synchronized(cancelGuard) {
                        if (cancelled) return
                        waitingOnNetwork = true
                    }
                }
            }
        } catch (e: Exception) {
        } finally {
            httpConn.disconnect()
        }
    }

    fun abort() {
        synchronized(cancelGuard) {
            httpConn.disconnect()
            cancelled = true
            if (waitingOnNetwork) stop()
        }
        join()
    }
}