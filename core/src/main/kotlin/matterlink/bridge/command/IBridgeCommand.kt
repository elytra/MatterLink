package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.config.PermissionConfig
import matterlink.config.cfg
import matterlink.handlers.TickHandler
import matterlink.instance
import matterlink.stripColorOut
import java.util.*

abstract class IBridgeCommand {
    abstract val help: String
    abstract val permLevel: Double
    open val timeout: Int = 20

    sealed class CommandEnvironment {
        abstract val uuid: UUID?
        abstract val username: String?

        data class BridgeEnv(
                val name: String,
                val userId: String,
                val platform: String,
                override val uuid: UUID?
        ) : CommandEnvironment() {
            override val username: String?
                get() = uuid?.let { instance.uuidToName(uuid) }
        }

        data class GameEnv(
                override val username: String,
                override val uuid: UUID
        ) : CommandEnvironment()

        fun respond(text: String, cause: String = "") {
            when (this) {
                is BridgeEnv -> {
                    MessageHandlerInst.transmit(
                            ApiMessage(
                                    text = text.stripColorOut
                            ),
                            cause = cause
                    )
                }
                is GameEnv -> {
                    instance.wrappedSendToPlayer(uuid, text)
                }
            }

        }
    }


    private var lastUsed: Int = 0

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
    abstract fun execute(alias: String, user: String, env: CommandEnvironment, args: String): Boolean

    fun canExecute(uuid: UUID?): Boolean {
        instance.trace("canExecute this: $this  uuid: $uuid permLevel: $permLevel")
        val canExec = getPermLevel(uuid) >= permLevel
        instance.trace("canExecute return $canExec")
        return canExec
    }

    open fun validate() = true

    companion object {
        fun getPermLevel(uuid: UUID?): Double {
            if (uuid == null) return cfg.command.defaultPermUnauthenticated
            return PermissionConfig.perms[uuid.toString()] ?: cfg.command.defaultPermAuthenticated
        }
    }
}