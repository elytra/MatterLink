package matterlink.handlers

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.instance
import matterlink.stripColorOut

object ChatProcessor {
    /**
     * @return cancel message flag
     */
    fun sendToBridge(user: String, msg: String, event: String, uuid: String? = null): Boolean {
        val message = msg.trim()
        if(uuid != null && BridgeCommandRegistry.handleCommand(message, user, uuid)) return true
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
        return false
    }
}
