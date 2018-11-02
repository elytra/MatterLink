package matterlink.command

import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.config.PermissionConfig
import matterlink.config.baseCfg
import matterlink.config.cfg

object CommandCoreML {
    val name = "ml"

    val aliases = listOf("matterlink")

    val usage = "ml <connect|disconnect|reload|permAccept>"

    suspend fun execute(args: Array<String>, user: String, uuid: String?): String {
        val cmd = args[0].toLowerCase()

        return when (cmd) {
            "connect" -> {
                MessageHandlerInst.start("Bridge connected by console", true)
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
            "permaccept" -> {
                val requestId = args.getOrNull(1)?.toLowerCase() ?: run {
                    return "no requestId passed"
                }
                val request = PermissionConfig.permissionRequests.getIfPresent(requestId.toLowerCase()) ?: run {
                    return "No request available"
                }
                val nonce = args.getOrNull(2)?.toUpperCase() ?: run {
                    return "no code passed"
                }
                if (request.nonce != nonce) {
                    return "nonce in request does not match"
                }
                val powerLevelArg = args.getOrNull(2)?.toDoubleOrNull()
                val powerLevel = powerLevelArg ?: run { return "permLevel cannot be parsed" }
                ?: request.powerlevel
                ?: return "no permLevel provided"
                PermissionConfig.add(request.uuid, powerLevel, "${request.user} Authorized by $user")
                PermissionConfig.permissionRequests.invalidate(requestId)
                "added ${request.user} (uuid: ${request.uuid}) with power level: $powerLevel"
            }
            else -> {
                "Invalid arguments for command! \n" +
                        "usage: ${CommandCoreAuth.usage}"
            }
        }
    }

}