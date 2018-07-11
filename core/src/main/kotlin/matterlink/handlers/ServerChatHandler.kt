package matterlink.handlers

import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.bridge.format
import matterlink.config.cfg
import matterlink.instance
import matterlink.logger
import matterlink.stripColorIn

object ServerChatHandler {

    /**
     * This method must be called every server tick with no arguments.
     */
    fun writeIncomingToChat() {
        if (MessageHandlerInst.queue.isNotEmpty())
            logger.debug("incoming: " + MessageHandlerInst.queue.toString())
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
                        logger.debug("Threw out message with unhandled event: ${nextMessage.event}")
                        logger.debug(" Message contents:")
                        logger.debug(" User: $user")
                        logger.debug(" Text: $text")
                        logger.debug(" JSON: $json")
                        return
                    }
                }
                instance.wrappedSendToPlayers(message)
            }
        }
    }
}
