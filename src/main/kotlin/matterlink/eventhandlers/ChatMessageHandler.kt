package matterlink.eventhandlers

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
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
