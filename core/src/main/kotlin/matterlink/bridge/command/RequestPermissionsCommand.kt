package matterlink.bridge.command

import matterlink.config.PermissionConfig
import matterlink.config.PermissionRequest
import matterlink.config.cfg
import matterlink.randomString

object RequestPermissionsCommand : IBridgeCommand() {
    val syntax = " Syntax: request [permissionLevel]"
    override val help: String = "Requests permissions on the bridge. $syntax"
    override val permLevel: Double
        get() = cfg.command.defaultPermAuthenticated

    override suspend fun execute(alias: String, user: String, env: CommandEnvironment, args: String): Boolean {

        val uuid = env.uuid
        if (uuid == null) {
            env.respond("$user is not authenticated ($env)")
            return true
        }

        val argList = args.split(' ', limit = 2)
        val requestedLevelArg = argList.getOrNull(0)
        val requestedLevel = requestedLevelArg?.takeIf { it.isNotEmpty() }?.let {
            it.toDoubleOrNull() ?: run {
                env.respond(
                    "cannot parse permlevel '$requestedLevelArg'\n" +
                            syntax
                )
                return true
            }
        }

        val nonce = randomString(length = 3).toUpperCase()

        val requestId = user.toLowerCase()

        PermissionConfig.permissionRequests.put(
            requestId,
            PermissionRequest(uuid = uuid, user = user, nonce = nonce, powerlevel = requestedLevel)
        )
        env.respond("please ask a op to accept your permission elevation with `/ml permAccept $requestId $nonce [permLevel]`")

        return true
    }
}