package matterlink.bridge.command

interface IBridgeCommand {
    val name: String
    fun call(args: String): Boolean
    val help: String
}