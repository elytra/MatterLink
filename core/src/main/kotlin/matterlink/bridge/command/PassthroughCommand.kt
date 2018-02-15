package matterlink.bridge.command

class PassthroughCommand(override val name: String) : IBridgeCommand {

    override fun call(args: String): Boolean {
        return true
    }

    override val help: String = "No help available for this command."

}