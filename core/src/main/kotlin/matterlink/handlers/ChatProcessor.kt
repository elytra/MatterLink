package matterlink.handlers

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.instance

object ChatProcessor {
    fun sendToBridge(user: String, msg: String, event: String) {
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
