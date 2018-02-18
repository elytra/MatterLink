package matterlink.bridge

import matterlink.instance
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.config.cfg

object ServerChatHandler {

    /**
     * This method must be called every server tick with no arguments.
     */
    fun writeIncomingToChat(tick: Int) {
        MessageHandler.checkConnection(tick)
        if (MessageHandler.rcvQueue.isNotEmpty())
            instance.debug("incoming: " + MessageHandler.rcvQueue.toString())
        val nextMessage = MessageHandler.rcvQueue.poll()

        if (nextMessage != null && nextMessage.gateway == cfg.connect.gateway) {
            if (!nextMessage.text.isBlank()) {
                val message = when (nextMessage.event) {
                    "user_action" -> nextMessage.format(cfg.formatting.action)
                    "" -> {
                        // try to handle command and do not handle as a chat message
                        if (BridgeCommandRegistry.handleCommand(nextMessage)) return
                        nextMessage.format(cfg.formatting.chat)
                    }
                    "join_leave" -> nextMessage.format(cfg.formatting.joinLeave)
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
