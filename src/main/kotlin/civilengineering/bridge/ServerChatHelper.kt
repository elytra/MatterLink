package civilengineering.bridge

import civilengineering.CivilEngineering
import civilengineering.cfg
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class ServerChatHelper {
    @SubscribeEvent
    fun onServerUpdate(event: TickEvent.ServerTickEvent) {
        if (MessageHandler.rcvQueue.isNotEmpty())
            CivilEngineering.logger.debug("incoming: " + MessageHandler.rcvQueue.toString())
        val nextMessage = MessageHandler.rcvQueue.poll()

        if (nextMessage != null && nextMessage.gateway == cfg!!.connect.gateway) {
            val user = nextMessage.username
            val text = nextMessage.text.trim()

            val message: String

            if (!text.isEmpty()) {
                val section: Char = '\u00A7'
                message = when (nextMessage.event) {
                    "user_action" -> "* $user $text"
                    "" -> "<$user> $text"
                    "join_leave" -> section.toString()+"6-- $user $text"
                    else -> ""
                }
                if (message.isNotEmpty())
                    FMLCommonHandler.instance().minecraftServerInstance.playerList.sendMessage(TextComponentString(message))
            }
        }
    }
}
