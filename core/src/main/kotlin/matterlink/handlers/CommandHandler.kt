package matterlink.handlers

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler

object CommandHandler {
    fun handleCommand(sender: String, args: String, type: String) {
        MessageHandler.transmit(ApiMessage(
                username = sender,
                text = args,
                event = type
        ))
    }
}
