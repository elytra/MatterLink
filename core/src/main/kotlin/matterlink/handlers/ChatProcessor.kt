package matterlink.handlers

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.config.cfg
import matterlink.instance
import matterlink.logger
import matterlink.stripColorOut
import java.util.*

object ChatProcessor {
    /**
     * @return cancel message flag
     */
    fun sendToBridge(user: String, msg: String, event: String, uuid: UUID? = null): Boolean {
        val message = msg.trim()
        if(uuid != null && BridgeCommandRegistry.handleCommand(message, user, uuid)) return true
        when {
            message.isNotBlank() -> MessageHandlerInst.transmit(
                    ApiMessage(
                            username = user.stripColorOut,
                            text = message.stripColorOut,
                            event = event
                    ).apply {
                        if(cfg.outgoing.avatar.enable) {
                            if(uuid != null)
                                avatar = cfg.outgoing.avatar.urlTemplate.replace("{uuid}", uuid.toString())
                        }
                    },
                    cause = "Message from $user"
            )
            else -> logger.warn("WARN: dropped blank message by '$user'")
        }
        return false
    }
}
