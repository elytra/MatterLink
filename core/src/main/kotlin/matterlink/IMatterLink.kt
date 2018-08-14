package matterlink

import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.bridge.command.IBridgeCommand
import matterlink.bridge.command.IMinecraftCommandSender
import matterlink.config.BaseConfig
import matterlink.config.cfg
import matterlink.update.UpdateChecker
import org.apache.logging.log4j.Logger
import java.util.*

lateinit var instance: IMatterLink
lateinit var logger: Logger

abstract class IMatterLink {
    abstract val mcVersion: String
    abstract val modVersion: String
    abstract val buildNumber: Int
    abstract val forgeVersion: String

    abstract fun commandSenderFor(user: String, env: IBridgeCommand.CommandEnvironment, op: Boolean): IMinecraftCommandSender

    abstract fun wrappedSendToPlayers(msg: String)

    abstract fun wrappedSendToPlayer(username: String, msg: String)
    abstract fun wrappedSendToPlayer(uuid: UUID, msg: String)
    abstract fun isOnline(username: String): Boolean
    abstract fun nameToUUID(username: String): UUID?
    abstract fun uuidToName(uuid: UUID): String?

    fun start() {
//        MessageHandlerInst.logger = { level, msg ->
//            when (level) {
//                "FATAL" -> logger.fatal(msg)
//                "ERROR" -> logger.error(msg)
//                "WARN" -> logger.warn(msg)
//                "INFO" -> logger.info(msg)
//                "DEBUG" -> logger.debug(msg)
//                "TRACE" -> logger.trace(msg)
//            }
//        }
        MessageHandlerInst.logger = logger
        serverStartTime = System.currentTimeMillis()

        if (cfg.connect.autoConnect)
            MessageHandlerInst.start("Server started, connecting to matterbridge API", true)
        UpdateChecker.run()
    }

    fun stop() {
        MessageHandlerInst.stop("Server shutting down, disconnecting from matterbridge API")
    }

//    abstract fun log(level: String, formatString: String, vararg data: Any)

//    fun fatal(formatString: String, vararg data: Any) = log("FATAL", formatString, *data)
//    fun error(formatString: String, vararg data: Any) = log("ERROR", formatString, *data)
//    fun warn(formatString: String, vararg data: Any) = log("WARN", formatString, *data)
//    fun info(formatString: String, vararg data: Any) = log("INFO", formatString, *data)
//
//    fun debug(formatString: String, vararg data: Any) {
//        if (cfg.debug.logLevel == "DEBUG" || cfg.debug.logLevel == "TRACE")
//            log("INFO", "DEBUG: " + formatString.replace("\n", "\nDEBUG: "), *data)
//    }
//
//    fun trace(formatString: String, vararg data: Any) {
//        if (cfg.debug.logLevel == "TRACE")
//            log("INFO", "TRACE: " + formatString.replace("\n", "\nTRACE: "), *data)
//    }

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

    abstract fun collectPlayers(area: Area): Set<UUID>

}