package civilengineering.bridge

import civilengineering.CivilEngineering
import civilengineering.cfg
import civilengineering.util.Color
import civilengineering.util.color
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
            val chan = nextMessage.channel

            val message: String

            if (!text.isEmpty()) {
                val section = '\u00A7'
                val event = nextMessage.event
                message = when (event) {
                    "user_action" -> "* $user $text"
                    "" -> "<$user> $text"
                    "join_leave" -> "-- $user $text $chan".color(Color.GOLD)
                    else -> {
                        CivilEngineering.logger.debug("Threw out message with unhandled event: $event")
                        CivilEngineering.logger.debug(" Message contents:")
                        CivilEngineering.logger.debug(" User: $user")
                        CivilEngineering.logger.debug(" Text: $text")
                        return
                    }
                }
                FMLCommonHandler.instance().minecraftServerInstance.playerList.sendMessage(TextComponentString(message))
            }
        }
    }
}
