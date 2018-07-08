package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.config.AuthRequest
import matterlink.config.IdentitiesConfig
import matterlink.config.cfg
import matterlink.instance
import matterlink.randomString

object AuthBridgeCommand : IBridgeCommand() {
    override val help: String = "Requests authentication on the bridge. Syntax: auth [username]"
    override val permLevel: Double
        get() = cfg.command.defaultPermUnauthenticated

    override fun execute(alias: String, user: String, userId: String, platform: String, uuid: String?, args: String): Boolean {

        if (uuid != null) {
            val name = instance.uuidToName(uuid)
            respond("you are already authenticated as name: $name uuid: $uuid")
            return true
        }

        val argList = args.split(' ', limit = 2)
        val target = argList.getOrNull(0) ?: run {
            respond("no username/uuid provided")
            return true
        }

        var targetUserName = target

        val targetUUid: String = instance.nameToUUID(target) ?: run {
            targetUserName = instance.uuidToName(target) ?: run {
                respond("cannot find player by username/uuid $target")
                return true
            }
            target
        }

        val online = instance.isOnline(targetUserName)
        if (!online) {
            respond("$targetUserName is not online, please log in and try again to send instructions")
            return true
        }
        val nonce = randomString(length = 3).toUpperCase()

        val requestId = user.toLowerCase()
        instance.wrappedSendToPlayer(targetUserName, "have you requested authentication with the MatterLink system?")
        instance.wrappedSendToPlayer(targetUserName, "if yes please execute /auth accept $user $nonce")
        instance.wrappedSendToPlayer(targetUserName, "otherwise you may ignore this message")


        IdentitiesConfig.authRequests.put(requestId, AuthRequest(username = targetUserName, uuid = targetUUid, nonce = nonce, platform = platform, userid = userId))
        respond("please accept the authentication request ingame, do not share the code")

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