package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.config.PermissionConfig
import matterlink.config.cfg
import matterlink.handlers.TickHandler
import matterlink.instance

abstract class IBridgeCommand {
    abstract val help: String
    abstract val permLevel: Double
    open val timeout: Int = 20

    protected var lastUsed: Int = 0

    val alias: String
        get() = BridgeCommandRegistry.getName(this)!!


    fun reachedTimeout(): Boolean {
        return (TickHandler.tickCounter - lastUsed > timeout)
    }

    fun preExecute() {
        lastUsed = TickHandler.tickCounter
    }

    /**
     *
     * @return consume message flag
     */
    abstract fun execute(alias: String, user: String, userId: String, platform: String, uuid: String?, args: String): Boolean

    fun canExecute(uuid: String?): Boolean {
        instance.trace("canExecute this: $this  uuid: $uuid permLevel: $permLevel")
        val canExec = getPermLevel(uuid) >= permLevel
        instance.trace("canExecute return $canExec")
        return canExec
    }

    open fun validate() = true

    companion object {
        fun getPermLevel(uuid: String?): Double {
            if(uuid == null) return cfg.command.defaultPermUnauthenticated
            return PermissionConfig.perms[uuid] ?: cfg.command.defaultPermAuthenticated
        }
    }
}