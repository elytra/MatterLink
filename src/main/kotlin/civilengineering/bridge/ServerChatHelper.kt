package civilengineering.bridge

import civilengineering.CivilEngineering
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class ServerChatHelper {
    @SubscribeEvent
    fun onServerUpdate(event: TickEvent.ServerTickEvent) {
        if (MessageHandler.rcvQueue.isNotEmpty())
            CivilEngineering.logger.info("incoming: " + MessageHandler.rcvQueue.toString())
        val nextMessage = MessageHandler.rcvQueue.poll()

        if (nextMessage != null) {
            val user = nextMessage.username
            val text = nextMessage.text.trim()

            val message: String

            if (!text.isEmpty()) {
                message = when (nextMessage.event) {
                    "user_action" -> "* $user $text"
                    else -> "<$user> $text"
                }
                FMLCommonHandler.instance().minecraftServerInstance.playerList.sendMessage(TextComponentString(message))
            }
        }
    }
}
