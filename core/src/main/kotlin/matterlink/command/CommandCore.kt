package matterlink.command

import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.config.PermissionConfig
import matterlink.config.baseCfg
import matterlink.config.cfg

object CommandCore {
    fun getName() = "bridge"

    fun getAliases() = listOf("BRIDGE", "bridge")

    fun getUsage() = "bridge <connect|disconnect|reload|acceptPerm>"

    fun execute(args: Array<String>, user: String): String {
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
                cfg = baseCfg.load()
                BridgeCommandRegistry.reloadCommands()
//                if (!MessageHandlerInst.connected)
                MessageHandlerInst.start("Bridge reconnected", false)
                "Bridge config reloaded!"
            }
            "acceptperm" -> {
                val key = args.getOrNull(1)?.toLowerCase() ?: return "No argument providing they request key"
                val request = PermissionConfig.permissionRequests[key] ?: return "No request found for key $key"
                val powerLevelArg = args.getOrNull(2)?.toDoubleOrNull()
                val powerLevel = powerLevelArg ?: request.powerlevel ?: return "no powerLevel provided or it cannot be parsed"
                PermissionConfig.add(request.platform, request.userId, powerLevel, "${request.user} Authorized by $user")
                PermissionConfig.permissionRequests.remove(key)
                "added ${request.user} (platform: ${request.platform} userId: ${request.userId}) with power level: $powerLevel"
            }
            else -> {
                "Invalid arguments for command!"
            }
        }
    }

}