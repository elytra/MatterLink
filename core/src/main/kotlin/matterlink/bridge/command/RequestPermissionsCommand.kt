package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.config.PermissionConfig
import matterlink.config.PermissionRequest
import matterlink.config.cfg
import matterlink.randomString

object RequestPermissionsCommand : IBridgeCommand() {
    override val help: String = "Requests permissions on the bridge. Syntax: request [permissionLevel]"
    override val permLevel: Double
        get() = cfg.command.defaultPermAuthenticated

    override fun execute(alias: String, user: String, userId: String, platform: String, uuid: String?, args: String): Boolean {

        if (uuid == null) {
            respond("$user is not authenticated (userid: $userId platform: $platform)")
            return true
        }

        val argList = args.split(' ', limit = 2)
        val requestedLevelArg = argList.getOrNull(0)
        val requestedlevel = requestedLevelArg?.let {
            it.toDoubleOrNull() ?: run {
                respond("cannot parse permlevel")
                return true
            }
        }

        val nonce = randomString(length = 3).toUpperCase()

        val requestId = user.toLowerCase()

        PermissionConfig.permissionRequests.put(requestId, PermissionRequest(uuid = uuid, user = user, nonce = nonce, powerlevel = requestedlevel))
        respond("please ask a op to accept your permission elevation with `/ml permAccept $requestId $nonce [powerlevel]`")

        return true
    }


    private fun respond(text: String) {
        MessageHandlerInst.transmit(
                ApiMessage(
                        text = text
                )
        )
    }

}