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

    fun fatal(formatString: String, vararg data: Any) = log(Level.FATAL.name, formatString, *data)
    fun error(formatString: String, vararg data: Any) = log(Level.ERROR.name, formatString, *data)
    fun warn(formatString: String, vararg data: Any) = log(Level.WARN.name, formatString, *data)
    fun info(formatString: String, vararg data: Any) = log(Level.INFO.name, formatString, *data)
    fun debug(formatString: String, vararg data: Any) = log(Level.DEBUG.name, formatString, *data)
    fun trace(formatString: String, vararg data: Any) = log(Level.TRACE.name, formatString, *data)

}