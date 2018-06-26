package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.config.PermissionConfig
import matterlink.config.PermissionRequest

object PermCommand : IBridgeCommand {
    override val help: String = "Requests permissions on the bridge. Syntax: req [powerlevel] [key]"
    override val permLevel = 0.0
    override fun execute(alias: String, user: String, userId: String, server: String, args: String): Boolean {
        val argList = args.split(' ', limit = 2)
        val requestedLevel = args.toDoubleOrNull() ?: 20.0
        var unfilteredKey = user
        var key = unfilteredKey.replace("[^A-Za-z0-9 ]".toRegex(), "").toLowerCase()
        if (argList.size > 1) {
            unfilteredKey = argList[1]
            key = unfilteredKey.replace("[^A-Za-z0-9 ]".toRegex(), "").toLowerCase()
        }
        if (key.isBlank()) {
            MessageHandlerInst.transmit(
                    ApiMessage(
                            text = "$unfilteredKey is made up of invalid characters.. please specifiy a key for tracking this request"
                    )
            )
            return true
        } else if (PermissionConfig.permissionRequests.containsKey(key)) {
            MessageHandlerInst.transmit(
                    ApiMessage(
                            text = "there is already a permission request for $key"
                    )
            )
            return true
        }
        val currentPowerlevel = IBridgeCommand.getPermLevel(userId, server)
        if(currentPowerlevel < 0.0) {
            MessageHandlerInst.transmit(
                    ApiMessage(
                            text = "Your level is $currentPowerlevel seems like someone banned you from making any more requests"
                    )
            )
            return true
        }

        MessageHandlerInst.transmit(
                ApiMessage(
                        text = "requet for powerlevel $requestedLevel from user $user userID: $userId server: $server\n" +
                                "accept this by executing `bridge acceptPerm $key <level>`\n" +
                                "setting a negative level will prevent people from sending any more requests"
                )
        )
        PermissionConfig.permissionRequests[key.toLowerCase()] = PermissionRequest(user, server, userId, requestedLevel)
        // PermissionConfig.add(server, userId, requestedlevel, "authorized by $user")
        return true
    }

}