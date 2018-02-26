package matterlink.bridge.command

import com.typesafe.config.ConfigException
import matterlink.config.PermissionConfig
import matterlink.instance
import matterlink.stackTraceString

interface IBridgeCommand {
    val help: String
    val permLevel: Int

    fun execute(alias: String, user: String, userId: String, server: String, args: String): Boolean

    fun canExecute(userId: String, server: String): Boolean {
        return getPermLevel(userId, server) >= permLevel
    }

    fun validate() = true

    companion object {
        fun getPermLevel(userId: String, server: String): Int {
            try {
                instance.debug("looking up server $server")
                instance.debug("looking up userId $userId")
                val serverMap = PermissionConfig.perms[server] ?: return 0
                serverMap.forEach { (permLevel, userIDs) ->
                    userIDs.forEach { if(it == userId) return permLevel }
                }
                return 0
            } catch (e: ConfigException.WrongType) {
                instance.warn(e.stackTraceString)
                return 0
            } catch (e: ConfigException.Missing) {
                return 0
            }
        }
    }
}