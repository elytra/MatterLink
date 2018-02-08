package matterlink.bridge.command

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
                    output = output + player + " "
                }

                MessageHandler.transmit(ApiMessage(
                        username = cfg!!.relay.systemUser,
                        text = output
                ))

                return true
            }
    )

    fun register() {
        BridgeCommand.registerAll(listPlayers)
    }
}