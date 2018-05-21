package matterlink.bridge.command

import matterlink.config.PermissionConfig
import matterlink.instance

interface IBridgeCommand {
    val help: String
    val permLevel: Double

    fun execute(alias: String, user: String, userId: String, server: String, args: String): Boolean

    fun canExecute(userId: String, server: String): Boolean {
        instance.info("this: $this canExecute: $userId server: $server permLevel: $permLevel")
        val canExec = getPermLevel(userId, server) >= permLevel
        instance.info("return $canExec")
        return canExec
    }

    fun validate() = true

    companion object {
        fun getPermLevel(userId: String, server: String): Double {
            val serverMap = PermissionConfig.perms[server] ?: return 0.0
            return serverMap[userId] ?: 0.0
        }
    }
}