package matterlink.bridge.command

import matterlink.instance

class PassthroughCommand(override val name: String, val cmd: String) : IBridgeCommand {

    override fun call(args: String): Boolean {
        return instance.commandSender.execute("$name $args",100)
    }

    override val help: String = "No help available for this command."

}