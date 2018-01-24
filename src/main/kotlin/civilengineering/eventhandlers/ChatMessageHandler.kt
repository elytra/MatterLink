package civilengineering.eventhandlers

import civilengineering.bridge.ApiMessage
import civilengineering.bridge.MessageHandler
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class ChatMessageHandler {
    @SubscribeEvent
    fun handleServerChatEvent(event: ServerChatEvent) {
        val message = event.message.trim()
        if (message.isNotBlank())
            MessageHandler.transmit(ApiMessage(
                    username = event.username,
                    text = message)
            )
    }
}
