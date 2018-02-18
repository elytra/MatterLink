package matterlink.bridge.command

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.instance

data class CustomCommand(
        override val alias: String,
        val type: CommandType = CommandType.RESPONSE,
        val execute: String = "",
        val response: String = "",
        override val permLevel: Int = 0,
        override val help: String = "",
        val allowArgs : Boolean = true
) : IBridgeCommand {
    override fun execute(user: String, userId: String, server: String, args: String): Boolean {
        if (!allowArgs && args.isNotBlank()) return false
        if (IBridgeCommand.getPermLevel(userId,server) < permLevel) {
            MessageHandler.transmit(ApiMessage(text = "$user is not permitted to perform command: $alias"))
            return false
        }

        return when (type) {
            CommandType.PASSTHROUGH -> {
                //TODO: use a new commandSender for each user
                instance.commandSender.execute("$execute $args")
            }
            CommandType.RESPONSE -> {
                //TODO: replace format variables, use arguments
                MessageHandler.transmit(ApiMessage(text = response))
                true
            }
        }
    }

    /**
     *
     */
    override fun validate(): Boolean {


        return true
    }
}

enum class CommandType {
    PASSTHROUGH, RESPONSE
}