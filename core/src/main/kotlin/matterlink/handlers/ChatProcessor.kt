package matterlink.handlers

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.instance
import matterlink.stripColor

object ChatProcessor {
    fun sendToBridge(user: String, msg: String, event: String) {
        val message = msg.trim()
        when {
            message.isNotBlank() -> MessageHandlerInst.transmit(ApiMessage(
                    username = user.stripColor,
                    text = message.stripColor,
                    event = event)
            )
            else -> instance.warn("WARN: dropped blank message by '$user'")
        }
    }
}
