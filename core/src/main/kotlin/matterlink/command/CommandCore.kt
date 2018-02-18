package matterlink.command

import matterlink.bridge.MessageHandler
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.config.cfg
import matterlink.instance

object CommandCore {
    fun getName() = "bridge"

    fun getAliases() = listOf("BRIDGE", "bridge")

    fun getUsage() = "bridge <connect|disconnect|reload>"

    fun execute(args: Array<String>): String {
        val cmd = args[0].toLowerCase()

        return when (cmd) {
            "connect" -> {
                instance.connect()
                "Attempting bridge connection!"
            }
            "disconnect" -> {
                instance.disconnect()
                "Bridge disconnected!"
            }
            "reload" -> {
                if (MessageHandler.connected) instance.disconnect()
                cfg = cfg.load()
                BridgeCommandRegistry.reloadCommands()
                if (!MessageHandler.connected) instance.connect()
                "Bridge config reloaded!"
            }
            else -> {
                "Invalid arguments for command!"
            }
        }
    }

}