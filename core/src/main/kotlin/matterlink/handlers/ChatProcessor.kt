package matterlink.handlers

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.instance
import matterlink.stripColorOut

object ChatProcessor {
    fun sendToBridge(user: String, msg: String, event: String) {
        val message = msg.trim()
        when {
            message.isNotBlank() -> MessageHandlerInst.transmit(
                    ApiMessage(
                            username = user.stripColorOut,
                            text = message.stripColorOut,
                            event = event
                    ),
                    cause = "Message from $user"
            )
            else -> instance.warn("WARN: dropped blank message by '$user'")
        }
    }
}
