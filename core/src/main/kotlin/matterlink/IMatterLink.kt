package matterlink

import matterlink.bridge.MessageHandler
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.bridge.command.IMinecraftCommandSender
import matterlink.config.cfg

lateinit var instance: IMatterLink

abstract class IMatterLink {
    abstract val mcVersion: String
    abstract val modVersion: String
    abstract val forgeVersion: String

    abstract fun commandSenderFor(user: String, userId: String, server: String): IMinecraftCommandSender

    abstract fun wrappedSendToPlayers(msg: String)


    fun start() {
        serverStartTime = System.currentTimeMillis()
        MessageHandler.start(clear = true, firstRun = true, message = "Server started, connecting to matterbridge API")
    }

    fun stop() {
        MessageHandler.stop(message = "Server shutting down, disconnecting from matterbridge API")
    }

    abstract fun log(level: String, formatString: String, vararg data: Any)

    fun fatal(formatString: String, vararg data: Any) = log("^FATAL", formatString, *data)
    fun error(formatString: String, vararg data: Any) = log("ERROR", formatString, *data)
    fun warn(formatString: String, vararg data: Any) = log("WARN", formatString, *data)
    fun info(formatString: String, vararg data: Any) = log("INFO", formatString, *data)
    fun debug(formatString: String, vararg data: Any) {
        if (cfg.debug.logLevel == "DEBUG" || cfg.debug.logLevel == "TRACE")
            log("INFO", "DEBUG: " + formatString.replace("\n", "\nDEBUG: "), *data)
    }

    fun trace(formatString: String, vararg data: Any) {
        if (cfg.debug.logLevel == "TRACE")
            log("INFO", "TRACE: " + formatString.replace("\n", "\nTRACE: "), *data)
    }

    /**
     * in milliseconds
     */
    var serverStartTime: Long = System.currentTimeMillis()

    fun getUptimeAsString(): String {
        val total = (System.currentTimeMillis() - serverStartTime) / 1000
        val s = total % 60
        val m = (total / 60) % 60
        val h = (total / 3600) % 24
        val d = total / 86400

        fun timeFormat(unit: Long, name: String) = when {
            unit > 1L -> "$unit ${name}s "
            unit == 1L -> "$unit $name "
            else -> ""
        }

        var result = ""
        result += timeFormat(d, "Day")
        result += timeFormat(h, "Hour")
        result += timeFormat(m, "Minute")
        result += timeFormat(s, "Second")
        return result
    }

    fun registerBridgeCommands() {
        BridgeCommandRegistry.reloadCommands()
    }
}