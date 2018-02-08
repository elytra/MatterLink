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
        val playerList = FMLCommonHandler.instance().minecraftServerInstance.playerList.onlinePlayerNames
        MessageHandler.transmit(ApiMessage(
                username = cfg!!.relay.systemUser,
                text = when {
                    playerList.isNotEmpty() -> "players: " + playerList.joinToString(" ") { it.antiping() }
                    else -> "No Players online"
                }
        ))

        return true
    }

}