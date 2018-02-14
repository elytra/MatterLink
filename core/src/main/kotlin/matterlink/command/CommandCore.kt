package matterlink.command

import matterlink.bridge.MessageHandler
import matterlink.config.BaseConfig
import matterlink.config.cfg
import matterlink.instance

object CommandCore {
    fun getName() : String { return "bridge" }
    fun getAliases() : List<String> { return listOf("BRIDGE","bridge") }
    fun getUsage() : String { return "bridge <connect|disconnect|reload>"}

    fun execute(args : Array<String>) : String {
        val cmd = args[0].toLowerCase()

        return when (cmd) {
            "connect" -> {
                instance.connect()
                "Bridge connected!"
            }
            "disconnect" -> {
                instance.disconnect()
                "Bridge disconnected!"
            }
            "reload" -> {
                if (MessageHandler.connected) instance.disconnect()
                cfg!!.reload(cfg!!.file)
                if (!MessageHandler.connected) instance.connect()
                "Bridge config reloaded!"
            }
            else -> {
                "Invalid arguments for command!"
            }
        }
    }

}