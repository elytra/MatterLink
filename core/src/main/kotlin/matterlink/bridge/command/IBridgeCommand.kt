package matterlink.bridge.command

import matterlink.config.PermissionConfig

interface IBridgeCommand {
    val help: String
    val permLevel: Double

    fun execute(alias: String, user: String, userId: String, server: String, args: String): Boolean

    fun canExecute(userId: String, server: String): Boolean {
        return getPermLevel(userId, server) >= permLevel
    }

    fun validate() = true

    companion object {
        fun getPermLevel(userId: String, server: String): Double {
            val serverMap = PermissionConfig.perms[server] ?: return 0.0
            return serverMap[userId] ?: 0.0
        }
    }
}