package matterlink.command

import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.config.cfg

object CommandCore {
    fun getName() = "bridge"

    fun getAliases() = listOf("BRIDGE", "bridge")

    fun getUsage() = "bridge <connect|disconnect|reload>"

    fun execute(args: Array<String>): String {
        val cmd = args[0].toLowerCase()

        return when (cmd) {
            "connect" -> {
                MessageHandlerInst.start( "Bridge connected by console", true)
                "Attempting bridge connection!"
            }
            "disconnect" -> {
                MessageHandlerInst.stop("Bridge disconnected by console")
                "Bridge disconnected!"
            }
            "reload" -> {
//                if (MessageHandlerInst.connected)
                MessageHandlerInst.stop("Bridge restarting (reload command issued by console)")
                cfg = cfg.load()
                BridgeCommandRegistry.reloadCommands()
//                if (!MessageHandlerInst.connected)
                MessageHandlerInst.start("Bridge reconnected", false)
                "Bridge config reloaded!"
            }
            else -> {
                "Invalid arguments for command!"
            }
        }
    }

}