package matterlink

import matterlink.bridge.MessageHandler
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.bridge.command.IMinecraftCommandSender
import matterlink.config.cfg
import matterlink.update.UpdateChecker
import java.time.Duration

lateinit var instance: IMatterLink

abstract class IMatterLink {
    abstract val mcVersion: String
    abstract val modVersion: String

    abstract fun commandSenderFor(user: String, userId: String, server: String): IMinecraftCommandSender

    abstract fun wrappedSendToPlayers(msg: String)


    fun start() {
        serverStartTime = System.currentTimeMillis()
        MessageHandler.start(clear = true, firstRun = true, message = "Server started, connecting to matterbridge API")
        if (cfg.update.enable) {
            Thread(UpdateChecker()).start()
        }

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
    var serverStartTime: Long = 0

    fun getUptimeInSeconds(): Int {
        return ((System.currentTimeMillis() - serverStartTime) / 1000).toInt()
    }

    fun getUptimeAsString(): String {
        val duration = Duration.ofSeconds((System.currentTimeMillis() - serverStartTime) / 1000)
        return duration.toString()
//        val total = this.getUptimeInSeconds()
//        val sec = total % 60
//        val min = (total / 60) % 60
//        val hr = (total / 3600) % 24
//        val day = total / 86400
//
//        return "${day}d ${hr}hr ${min}m ${sec}s"
    }

    fun registerBridgeCommands() {
        BridgeCommandRegistry.reloadCommands()
    }
}