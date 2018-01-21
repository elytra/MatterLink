package civilengineering.bridge

import civilengineering.CivilEngineering
import civilengineering.Config
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue

class MessageHandler : Runnable {

    override fun run() {
        CivilEngineering.logger!!.info("Connecting to bridge server @ " + Config.connectURL)
        try {
            while (true) {
                transmitFromQueue()
//                receiveToQueue()
                sleep(1000)
            }
        } catch (e: Exception) {

            if (e is InterruptedException) {
                CivilEngineering.logger!!.info("Connection closed.")
            } else if (e is IOException) {
                CivilEngineering.logger!!.error("Error connecting to bridge server!")
                CivilEngineering.logger!!.error(e.message)

            }
        }
    }

    @Throws(IOException::class)
    private fun transmitFromQueue() {
        var nextMessage: ApiMessage? = xmitQueue.poll()
        while (nextMessage != null) {
            //open a connection
            val url = URL(Config.connectURL + "/api/message")
            val urlConnection = url.openConnection()
            val connection = urlConnection as HttpURLConnection

            //configure the connection
            connection.allowUserInteraction = false
            connection.instanceFollowRedirects = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.requestMethod = "POST"
            if (Config.authToken.isNotEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + Config.authToken)
            }

            //encode the ApiMessage for sending
            val json = nextMessage.encode()

            //send the message
            connection.doOutput = true
            val post = DataOutputStream(connection.outputStream)
            post.writeBytes(json)
            post.flush()
            post.close()

            if (connection.responseCode != 200) {
                CivilEngineering.logger!!.error("Server returned " + connection.responseCode)
                break
            }
            nextMessage = xmitQueue.poll()
        }
    }

    @Throws(IOException::class)
    private fun receiveToQueue() {
        val messages: Array<ApiMessage>

        //open a connection
        val url = URL(Config.connectURL + "/api/messages")
        val con = url.openConnection() as HttpURLConnection

        //configure the connection
        con.allowUserInteraction = false
        con.instanceFollowRedirects = true
        if (Config.authToken.isNotEmpty()) {
            con.setRequestProperty("Authorization", "Bearer " + Config.authToken)
        }

        //read the messages
        val input = BufferedReader(InputStreamReader(con.inputStream))
        val data = StringBuilder()
        var line: String?
        while (true) {
            line = input.readLine()
            if (line == null) {
                break
            }
            data.append(line)
        }
        //decode the messages
        val gson = Gson()
        messages = gson.fromJson(data.toString(), Array<ApiMessage>::class.java)

        //enqueue the messages
        if (messages.isNotEmpty()) for (msg in messages) rcvQueue.add(msg)
    }

    companion object {

        private fun createThread(): CancellableConnectionFollowThread {
            return CancellableConnectionFollowThread(
                    {
                        CivilEngineering.logger!!.info("Connecting to bridge server @ " + Config.connectURL)
                        val httpConn = URL(Config.connectURL + "/api/stream").openConnection() as HttpURLConnection
                        if (Config.authToken.isNotBlank())
                            httpConn.setRequestProperty("Authorization", "Bearer ${Config.authToken}")
                        httpConn
                    },
                    {
                        rcvQueue.add(
                                ApiMessage.decode(it)
                        )
                        CivilEngineering.logger!!.trace("received: " + it)
                    }
            )
        }

        private var cancellableThread: CancellableConnectionFollowThread = createThread()

        private var xmitQueue = ConcurrentLinkedQueue<ApiMessage>()

        var rcvQueue = ConcurrentLinkedQueue<ApiMessage>()

        fun transmit(msg: ApiMessage) {
            CivilEngineering.logger!!.info("transmitting " + msg)
            transmitMessage(msg)
            //TODO: create thread with Runnable(sendstuff).execute()
        }

        fun stop() {
            cancellableThread.abort()
            CivilEngineering.logger!!.info("bridge closed ")
        }

        fun start(): Boolean {
            if (cancellableThread.isInterrupted) {
                CivilEngineering.logger!!.info("brebuilding bridge")
                cancellableThread = createThread()
            }
            if (!cancellableThread.isAlive) {
                cancellableThread.start()
                return true
            }
            return false
        }

        @Throws(IOException::class)
        private fun transmitMessage(message: ApiMessage) {
            //open a connection
            val url = URL(Config.connectURL + "/api/message")
            val urlConnection = url.openConnection()
            val connection = urlConnection as HttpURLConnection

            //configure the connection
            connection.allowUserInteraction = false
            connection.instanceFollowRedirects = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.requestMethod = "POST"
            if (Config.authToken.isNotEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + Config.authToken)
            }

            //encode the ApiMessage for sending
            val json = message.encode()

            //send the message
            connection.doOutput = true
            val post = DataOutputStream(connection.outputStream)
            post.writeBytes(json)
            post.flush()
            post.close()

            if (connection.responseCode != 200) {
                CivilEngineering.logger!!.error("Server returned " + connection.responseCode)
            }
        }
    }
}
