package matterlink

import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.bridge.command.IBridgeCommand
import matterlink.bridge.command.IMinecraftCommandSender
import matterlink.config.cfg
import matterlink.update.UpdateChecker
import java.util.UUID

lateinit var logger: Logger

lateinit var instance: IMatterLink

abstract class IMatterLink {
    abstract val mcVersion: String
    abstract val modVersion: String
    abstract val buildNumber: Int
    abstract val forgeVersion: String

    abstract fun commandSenderFor(
        user: String,
        env: IBridgeCommand.CommandEnvironment,
        op: Boolean
    ): IMinecraftCommandSender

    abstract fun wrappedSendToPlayers(msg: String)

    abstract fun wrappedSendToPlayer(username: String, msg: String)
    abstract fun wrappedSendToPlayer(uuid: UUID, msg: String)
    abstract fun isOnline(username: String): Boolean
    abstract fun nameToUUID(username: String): UUID?
    abstract fun uuidToName(uuid: UUID): String?

    suspend fun start() {
        MessageHandlerInst.logger = logger
        serverStartTime = System.currentTimeMillis()

        if (cfg.connect.autoConnect)
            MessageHandlerInst.start("Server started, connecting to matterbridge API", true)
        UpdateChecker.check()
    }

    suspend fun stop() {
        MessageHandlerInst.stop("Server shutting down, disconnecting from matterbridge API")
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

    abstract fun collectPlayers(area: Area): Set<UUID>

}