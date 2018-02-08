package matterlink.bridge.command

import matterlink.antiping
import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.cfg
import net.minecraftforge.fml.common.FMLCommonHandler

object PlayerListCommand : IBridgeCommand {
    override val name: String = "players"
    override val help: String = "Lists online players."
    override fun call(args: String): Boolean {
        if (args.isNotBlank()) return false

        MessageHandler.transmit(ApiMessage(
                username = cfg!!.relay.systemUser,
                text = FMLCommonHandler.instance().minecraftServerInstance.playerList.onlinePlayerNames.joinToString(" ") { it.antiping() }
        ))

        return true
    }

}