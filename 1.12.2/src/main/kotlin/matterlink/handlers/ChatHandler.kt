package matterlink.handlers

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler

object ChatHandler {
    fun handleChat(user: String, msg: String) {
        val message = msg.trim()
        if (message.isNotBlank())
            MessageHandler.transmit(ApiMessage(
                    username = user,
                    text = message
            ))
    }
}
