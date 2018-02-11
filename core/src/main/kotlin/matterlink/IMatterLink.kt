package matterlink

import matterlink.bridge.MessageHandler

lateinit var instance: IMatterLink

abstract class IMatterLink {
//    var interrupted: Boolean = false

    abstract fun wrappedSendToPlayers(msg: String)

    abstract fun wrappedPlayerList(): Array<String>

    fun connect() {
        MessageHandler.start(clear = true)
    }

    fun disconnect() {
        MessageHandler.stop()
    }

    abstract fun log(level: String, formatString: String, vararg data: Any)

    fun fatal(formatString: String, vararg data: Any) = log("^FATAL", formatString, *data)
    fun error(formatString: String, vararg data: Any) = log("ERROR", formatString, *data)
    fun warn(formatString: String, vararg data: Any) = log("WARN", formatString, *data)
    fun info(formatString: String, vararg data: Any) = log("INFO", formatString, *data)
    fun debug(formatString: String, vararg data: Any) = log("DEBUG", formatString, *data)
    fun trace(formatString: String, vararg data: Any) = log("TRACE", formatString, *data)

}