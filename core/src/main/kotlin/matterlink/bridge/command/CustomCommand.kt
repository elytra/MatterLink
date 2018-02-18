package matterlink.bridge.command

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.instance
import matterlink.lazyFormat

data class CustomCommand(
        override val alias: String,
        val type: CommandType = CommandType.RESPONSE,
        val execute: String = "",
        val response: String = "",
        override val permLevel: Int = 0,
        override val help: String = "",
        val allowArgs: Boolean = true
) : IBridgeCommand {
    override fun execute(user: String, userId: String, server: String, args: String): Boolean {
        if (!allowArgs && args.isNotBlank()) return false
        if (!canExecute(userId, server)) {
            MessageHandler.transmit(ApiMessage(text = "$user is not permitted to perform command: $alias"))
            return false
        }

        return when (type) {
            CommandType.PASSTHROUGH -> {
                //uses a new commandsender for each user
                // TODO: cache CommandSenders
                instance.commandSenderFor(user, userId, server).execute("$execute $args")
            }
            CommandType.RESPONSE -> {
                MessageHandler.transmit(ApiMessage(text = response.lazyFormat(getReplacements(user, args))))
                true
            }
        }
    }

    /**
     *
     */
    override fun validate(): Boolean {
        val typeCheck = when (type) {
            CommandType.PASSTHROUGH -> execute.isNotBlank()
            CommandType.RESPONSE -> response.isNotBlank()
        }
        if (!typeCheck) return false

        return true
    }

    fun getReplacements(user: String, args: String): Map<String, () -> String> = mapOf(
            "{UPTIME}" to instance::getUptimeAsString,
            "{USER}" to { user },
            "{ARGS}" to { args }
    )
}

enum class CommandType {
    PASSTHROUGH, RESPONSE
}