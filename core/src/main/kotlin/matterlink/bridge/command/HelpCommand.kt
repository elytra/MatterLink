package matterlink.bridge.command

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.config.cfg

object HelpCommand : IBridgeCommand {
    override val name: String = "help"
    override val help: String = "Returns the help string for the given command. Syntax: help <command>"
    override fun call(args: String): Boolean {
        val msg: String = if (args.isEmpty()) {
            "Available commands: ${ BridgeCommandRegistry.commandList}"
        } else {
            args.split(" ", ignoreCase = false)
                    .joinToString(separator = "\n") { "$it: ${ BridgeCommandRegistry.getHelpString(it) }" }
        }
        MessageHandler.transmit(ApiMessage(
                username = cfg!!.relay.systemUser,
                text = msg
        ))
        return true
    }

}