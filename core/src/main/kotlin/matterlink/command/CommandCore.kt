package matterlink.command

import matterlink.bridge.ApiMessage
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
                MessageHandler.start(message = "Bridge connected by console")
                "Attempting bridge connection!"
            }
            "disconnect" -> {
                MessageHandler.stop(message = "Bridge disconnected by console")
                "Bridge disconnected!"
            }
            "reload" -> {
                if (MessageHandler.connected) MessageHandler.stop(message = "Bridge restarting (reload command issued by console)")
                cfg = cfg.load()
                BridgeCommandRegistry.reloadCommands()
                if (!MessageHandler.connected) MessageHandler.start(message = "Bridge reconnected")
                "Bridge config reloaded!"
            }
            else -> {
                "Invalid arguments for command!"
            }
        }
    }

}