package matterlink.bridge.command

import matterlink.antiping
import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.cfg
import net.minecraftforge.fml.common.FMLCommonHandler

object Commands {
    val listPlayers = BridgeCommand(
            "players",
            fun(args: String): Boolean {
                if (args.isNotEmpty()) return false

                var output: String = ""

                for (player: String in FMLCommonHandler.instance().minecraftServerInstance.playerList.onlinePlayerNames) {
                    output = output + player.antiping() + " "
                }

                MessageHandler.transmit(ApiMessage(
                        username = cfg!!.relay.systemUser,
                        text = output
                ))

                return true
            },
            "Lists online players."
    )

    val help = BridgeCommand(
            "help",
            fun(args: String): Boolean {
                var msg: String
                if (args.isEmpty()) {
                    msg = "Available commands: " + BridgeCommand.listCommands()
                } else {
                    val cmd = args.split(delimiters = *charArrayOf(' '), ignoreCase = false, limit = 2)[0]
                    msg = cmd + ": " + BridgeCommand.getHelpString(cmd)
                }
                MessageHandler.transmit(ApiMessage(
                        username = cfg!!.relay.systemUser,
                        text = msg
                ))
                return true
            },
            "Returns the help string for the given command. Syntax: help <command>"
    )

    fun register() {
        BridgeCommand.registerAll(listPlayers, help)
    }
}