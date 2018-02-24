package matterlink.bridge.command

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.handlers.TickHandler
import matterlink.instance
import matterlink.lazyFormat

data class CustomCommand(
        override val alias: String,
        val type: CommandType = CommandType.RESPONSE,
        val execute: String = "",
        val response: String = "",
        override val permLevel: Int = 0,
        override val help: String = "",
        val allowArgs: Boolean = true,
        val timeout: Int = 20
) : IBridgeCommand {

    var lastUsed: Int = 0
    override fun execute(user: String, userId: String, server: String, args: String): Boolean {
        if (!allowArgs && args.isNotBlank()) return false

        if (TickHandler.tickCounter - lastUsed < timeout)
        {
            instance.debug("dropped command $alias")
            return true //eat command silently
        }

        if (!canExecute(userId, server)) {
            MessageHandler.transmit(ApiMessage(text = "$user is not permitted to perform command: $alias"))
            return false
        }

        lastUsed = TickHandler.tickCounter

        return when (type) {
            CommandType.EXECUTE -> {
                //uses a new commandsender for each user
                // TODO: cache CommandSenders
                val commandSender = instance.commandSenderFor(user, userId, server)
                commandSender.execute("$execute $args") || commandSender.reply.isNotBlank()
            }
            CommandType.RESPONSE -> {
                MessageHandler.transmit(ApiMessage(text = response.lazyFormat(getReplacements(user, userId, server, args))))
                true
            }
        }
    }

    /**
     *
     */
    override fun validate(): Boolean {
        val typeCheck = when (type) {
            CommandType.EXECUTE -> execute.isNotBlank()
            CommandType.RESPONSE -> response.isNotBlank()
        }
        if (!typeCheck) return false

        return true
    }

    fun getReplacements(user: String, userId: String, server: String, args: String): Map<String, () -> String> = mapOf(
            "{uptime}" to instance::getUptimeAsString,
            "{user}" to { user },
            "{userid}" to { userId },
            "{server}" to { server },
            "{args}" to { args }
    )
}

enum class CommandType {
    EXECUTE, RESPONSE
}