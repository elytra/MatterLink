package matterlink.bridge.command

interface BridgeCommand {
    val name: String
    fun command(args: String): Boolean
    val help: String
}