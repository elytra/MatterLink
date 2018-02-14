package matterlink.command

import matterlink.instance

object CommandCore {
    fun getName() : String { return "bridge" }
    fun getAliases() : List<String> { return listOf("BRIDGE","bridge") }
    fun getUsage() : String { return "bridge <connect|disconnect|reload>"}

    fun execute(args : Array<String>) : String {
        val cmd = args[0].toLowerCase()

        val reply : String = when (cmd) {
            "connect" -> {
                instance.connect()
                "Bridge connected!"
            }
            "disconnect" -> {
                instance.disconnect()
                "Bridge disconnected!"
            }
            "reload" -> {

                "Bridge config reloaded!"
            }
            else -> {
                "Invalid arguments for command!"
            }
        }

        return reply
    }

}