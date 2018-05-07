package matterlink.handlers

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.instance

object ChatProcessor {
    fun sendToBridge(user: String, msg: String, event: String) {
        val message = msg.trim()
        when {
            message.isNotBlank() -> MessageHandlerInst.transmit(ApiMessage()
                    .setUsername(user)
                    .setText(message)
                    .setEvent(event)
            )
            else -> instance.warn("WARN: dropped blank message by '$user'")
        }
    }
}
