package matterlink.bridge.command

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.cfg

object HelpCommand : BridgeCommand {
    override val name: String = "help"
    override val help: String = "Returns the help string for the given command. Syntax: help <command>"
    override fun command(args: String): Boolean {
        val msg: String = if (args.isEmpty()) {
            "Available commands: " + BridgeCommandRegistry.listCommands()
        } else {
            val cmd = args.split(delimiters = *charArrayOf(' '), ignoreCase = false, limit = 2)[0]
            cmd + ": " + BridgeCommandRegistry.getHelpString(cmd)
        }
        MessageHandler.transmit(ApiMessage(
                username = cfg!!.relay.systemUser,
                text = msg
        ))
        return true
    }

}