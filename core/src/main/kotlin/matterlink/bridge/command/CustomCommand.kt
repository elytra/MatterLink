package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.handlers.TickHandler
import matterlink.instance
import matterlink.lazyFormat
import matterlink.stripColorIn
import matterlink.stripColorOut

data class CustomCommand(
        val type: CommandType = CommandType.RESPONSE,
        val execute: String? = null,
        val response: String? = null,
        override val permLevel: Double = 0.0,
        override val help: String = "",
        val timeout: Int = 20,
        val defaultCommand: Boolean? = null,
        val execOp: Boolean? = null,
        val argumentsRegex: Regex? = null
) : IBridgeCommand {
    val alias: String
        get() = BridgeCommandRegistry.getName(this)!!

    @Transient
    private var lastUsed: Int = 0

    override fun execute(alias: String, user: String, userId: String, server: String, args: String): Boolean {
        if(argumentsRegex != null) {
            instance.debug("testing '$args' against '${argumentsRegex.pattern}'")
            if(!argumentsRegex.matches(args)) {
                MessageHandlerInst.transmit(
                        ApiMessage(
                                text = "$user sent invalid input to command $alias".stripColorOut
                        )
                )
                return false
            }
        }

        if (TickHandler.tickCounter - lastUsed < timeout) {
            instance.debug("dropped command $alias")
            return true //eat command silently
        }

        if (!canExecute(userId, server)) {
            MessageHandlerInst.transmit(
                    ApiMessage(
                            text = "$user is not permitted to perform command: $alias".stripColorOut
                    )
            )
            return false
        }

        lastUsed = TickHandler.tickCounter


        return when (type) {
            CommandType.EXECUTE -> {
                // uses a new commandsender for each use
                val commandSender = instance.commandSenderFor(user, userId, server, execOp ?: false)
                val cmd = execute?.lazyFormat(getReplacements(user, userId, server, args))?.stripColorIn ?: return false
                commandSender.execute(cmd) || commandSender.reply.isNotBlank()
            }
            CommandType.RESPONSE -> {
                MessageHandlerInst.transmit(
                        ApiMessage(
                                text = (response?.lazyFormat(getReplacements(user, userId, server, args))
                                        ?: "")
                        )
                )
                true
            }
        }
    }

    /**
     *
     */
    override fun validate(): Boolean {
        val typeCheck = when (type) {
            CommandType.EXECUTE -> execute?.isNotBlank() ?: false
            CommandType.RESPONSE -> response?.isNotBlank() ?: false
        }
        if (!typeCheck) return false

        return true
    }

    companion object {
        val DEFAULT = CustomCommand()

        fun getReplacements(user: String, userId: String, server: String, args: String): Map<String, () -> String> = mapOf(
                "{uptime}" to instance::getUptimeAsString,
                "{user}" to { user },
                "{userid}" to { userId },
                "{server}" to { server },
                "{args}" to { args }
        )
    }
}

enum class CommandType {
    EXECUTE, RESPONSE
}