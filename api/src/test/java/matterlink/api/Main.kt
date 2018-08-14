package matterlink.api

import com.google.gson.Gson
import com.google.gson.stream.JsonReader

import java.io.*
import java.util.Scanner

/**
 * Created by nikky on 07/05/18.
 *
 * @author Nikky
 * @version 1.0
 */
object Main {
    @Throws(IOException::class)
    @JvmStatic
    fun main(vararg args: String) {

        val handler = MessageHandler()
        val queue = handler.queue
        val config: Config

        val gson = Gson()
        try {
            val reader = JsonReader(FileReader("config.json"))
            config = gson.fromJson(reader, Config::class.java)
            handler.config = config
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            FileWriter("config.json").use { writer -> gson.toJson(handler.config, writer) }
        }

//        handler.logger = { level, msg -> System.out.printf("[%s] %s%n", level, msg) }
        handler.start("Connecting..", true)

        Thread {
            while (true) {
                val next = queue.poll()
                if (next != null) {
                    println(next)
                }
                try {
                    Thread.sleep(200)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }.start()

        Thread {
            while (true) {
                handler.checkConnection()
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }.start()

        val sc = Scanner(System.`in`)
        while (true) {
            val input = sc.nextLine()
            when (input) {
                "start" -> {
                    handler.start("start", false)
                }
                "stop" -> {
                    handler.stop("stop")
                }
                else -> {
                    handler.transmit(ApiMessage(text = input))
                }
            }
        }
    }
}
