package matterlink.bridge.command

import matterlink.antiping
import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.cfg
import net.minecraftforge.fml.common.FMLCommonHandler

object PlayerListCommand : BridgeCommand {
    override val name: String = "players"
    override val help: String = "Lists online players."
    override fun command(args: String): Boolean {
        if (args.isNotBlank()) return false

        var output = ""

        for (player: String in FMLCommonHandler.instance().minecraftServerInstance.playerList.onlinePlayerNames) {
            output = output + player.antiping() + " "
        }

        MessageHandler.transmit(ApiMessage(
                username = cfg!!.relay.systemUser,
                text = output
        ))

        return true
    }

}