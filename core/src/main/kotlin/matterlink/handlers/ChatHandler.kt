package matterlink.handlers

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.instance

object ChatHandler {
    fun handleChat(user: String, msg: String) {
        val message = msg.trim()
        when {
            message.isNotBlank() -> MessageHandler.transmit(ApiMessage(
                    username = user,
                    text = message
            ))
            else -> instance.warn("WARN: dropped blank message by '$user'")
        }
    }
}
