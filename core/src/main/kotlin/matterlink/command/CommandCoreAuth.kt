package matterlink.command

import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.config.IdentitiesConfig
import matterlink.config.PermissionConfig
import matterlink.config.baseCfg
import matterlink.config.cfg

object CommandCoreAuth {
    val name = "auth"

    val aliases = listOf("authenticate")

    val usage = "auth <accept|reject> <id> <code>"

    fun execute(args: Array<String>, user: String, uuid: String?): String {
        val cmd = args[0].toLowerCase()

        return when (cmd) {
            "accept" -> {
                val requestId = args.getOrNull(1)?.toLowerCase() ?: run {
                    return "no requestId passed"
                }
                val request = IdentitiesConfig.authRequests.getIfPresent(requestId.toLowerCase()) ?: run {
                    return "No request available"
                }
                val nonce = args.getOrNull(2)?.toUpperCase() ?: run {
                    return "no code passed"
                }
                if(request.nonce != nonce) {
                    return "nonce in request does not match"
                }
                if(request.username != user) {
                    return "username in request does not match ${request.username} != $user"
                }
                if(request.uuid != uuid) {
                    return "uuid in request does not match ${request.uuid} != $uuid"
                }

                IdentitiesConfig.add(request.uuid, request.username, request.platform, request.userid, "Accepted by $user")

                IdentitiesConfig.authRequests.invalidate(requestId)
                "${request.userid} on ${request.platform} is now identified as $user"
            }
            "reject" -> {

                val requestId = args.getOrNull(1)?.toLowerCase() ?: run {
                    return "no requestId passed"
                }
                val request = IdentitiesConfig.authRequests.getIfPresent(requestId.toLowerCase()) ?: run {
                    return "No request available"
                }
                val nonce = args.getOrNull(2)?.toUpperCase() ?: run {
                    return "no code passed"
                }
                if(request.nonce != nonce) {
                    return "nonce in request does not match"
                }
                if(request.username != user) {
                    return "username in request does not match ${request.username} != $user"
                }
                if(request.uuid != uuid) {
                    return "uuid in request does not match ${request.uuid} != $uuid"
                }

                IdentitiesConfig.authRequests.invalidate(requestId)
                "request $nonce for ${request.userid} on ${request.platform} was invalidated"
            }
            else -> {
                "Invalid arguments for command! \n" +
                        "usage: $usage"
            }
        }
    }

}