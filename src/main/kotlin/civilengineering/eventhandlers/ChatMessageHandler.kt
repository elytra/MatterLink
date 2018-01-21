package civilengineering.eventhandlers

import civilengineering.bridge.ApiMessage
import civilengineering.bridge.MessageHandler
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class ChatMessageHandler {
    @SubscribeEvent
    fun handleServerChatEvent(event: ServerChatEvent) {
        val message = event.message.trim { it <= ' ' }
        if (!message.isEmpty())
            MessageHandler.transmit(ApiMessage(event.username, message))
    }
}
