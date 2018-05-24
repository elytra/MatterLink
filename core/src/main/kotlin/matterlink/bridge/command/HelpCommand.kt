package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.stripColorOut

object HelpCommand : IBridgeCommand {
    override val help: String = "Returns the help string for the given command. Syntax: help <command>"
    override val permLevel = 0.0
    override fun execute(alias: String, user: String, userId: String, server: String, args: String): Boolean {
        val msg: String = when {
            args.isEmpty() ->
                "Available commands: ${BridgeCommandRegistry.getCommandList(IBridgeCommand.getPermLevel(userId, server))}"
            else -> args.split(" ", ignoreCase = false)
                    .joinToString(separator = "\n") {
                        "$it: ${BridgeCommandRegistry.getHelpString(it)}"
                    }
        }
        MessageHandlerInst.transmit(
                ApiMessage(
                        _text = msg.stripColorOut
                )
        )
        return true
    }

}