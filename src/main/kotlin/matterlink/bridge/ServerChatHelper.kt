package matterlink.bridge

import matterlink.MatterLink
import matterlink.cfg
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class ServerChatHelper {
    @SubscribeEvent
    fun onServerUpdate(event: TickEvent.ServerTickEvent) {
        if (MessageHandler.rcvQueue.isNotEmpty())
            MatterLink.logger.debug("incoming: " + MessageHandler.rcvQueue.toString())
        val nextMessage = MessageHandler.rcvQueue.poll()

        if (nextMessage != null && nextMessage.gateway == cfg!!.connect.gateway) {
            if (!nextMessage.text.isBlank()) {
                val section = '\u00A7'
                val message = when (nextMessage.event) {
                    "user_action" -> nextMessage.format(cfg!!.formatting.action)
                    "" -> nextMessage.format(cfg!!.formatting.chat)
                    "join_leave" -> nextMessage.format(cfg!!.formatting.joinLeave)
                    else -> {
                        val user = nextMessage.username
                        val text = nextMessage.text
                        val json = nextMessage.encode()
                        MatterLink.logger.debug("Threw out message with unhandled event: $event")
                        MatterLink.logger.debug(" Message contents:")
                        MatterLink.logger.debug(" User: $user")
                        MatterLink.logger.debug(" Text: $text")
                        MatterLink.logger.debug(" JSON: $json")
                        return
                    }
                }
                FMLCommonHandler.instance().minecraftServerInstance.playerList.sendMessage(TextComponentString(message))
            }
        }
    }
}
