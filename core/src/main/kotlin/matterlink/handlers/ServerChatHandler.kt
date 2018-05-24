package matterlink.handlers

import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.bridge.format
import matterlink.config.cfg
import matterlink.instance
import matterlink.stripColorIn

object ServerChatHandler {

    /**
     * This method must be called every server tick with no arguments.
     */
    fun writeIncomingToChat() {
        if (MessageHandlerInst.queue.isNotEmpty())
            instance.debug("incoming: " + MessageHandlerInst.queue.toString())
        val nextMessage = MessageHandlerInst.queue.poll() ?: null

        if (nextMessage?.gateway == cfg.connect.gateway) {
            if (!nextMessage.text.isBlank()) {
                nextMessage.text = nextMessage.text.stripColorIn
                val message = when (nextMessage.event) {
                    "user_action" -> nextMessage.format(cfg.incoming.action)
                    "" -> {
                        // try to handle command and do not handle as a chat message
                        if (BridgeCommandRegistry.handleCommand(nextMessage)) return
                        nextMessage.format(cfg.incoming.chat)
                    }
                    "join_leave" -> nextMessage.format(cfg.incoming.joinPart)
                    else -> {
                        val user = nextMessage.username
                        val text = nextMessage.text
                        val json = nextMessage.encode()
                        instance.debug("Threw out message with unhandled event: ${nextMessage.event}")
                        instance.debug(" Message contents:")
                        instance.debug(" User: $user")
                        instance.debug(" Text: $text")
                        instance.debug(" JSON: $json")
                        return
                    }
                }
                instance.wrappedSendToPlayers(message)
            }
        }
    }
}
