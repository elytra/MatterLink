package matterlink.handlers

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.bridge.format
import matterlink.config.cfg
import matterlink.instance
import matterlink.logger
import java.util.*

object ServerChatHandler {

    /**
     * This method must be called every server tick with no arguments.
     */
    fun writeIncomingToChat() {
        if (MessageHandlerInst.queue.isNotEmpty())
            logger.debug("incoming: " + MessageHandlerInst.queue.toString())
        val nextMessage = MessageHandlerInst.queue.poll() ?: return

        val defaults = cfg.incomingDefaults

        val sourceGateway = nextMessage.gateway

        // find all areas to send to

        val targetUUIDs = mutableSetOf<UUID>()
        val skips = mutableSetOf<String>()

        val locations = cfg.locations.filter {
            it.gateway == sourceGateway
        }

        if(nextMessage.event.isEmpty()) {
            // filter command handlers
            val commandLocations = locations.filter {
                it.incoming.commands ?: cfg.incomingDefaults.commands
            }

            // process potential command
            for (( label, location) in commandLocations) {
                if (BridgeCommandRegistry.handleCommand(nextMessage)) return
            }
        }



        for (location in locations) {
            val label = location.label
            if(skips.contains(label)) {
                logger.debug("skipping $label")
                continue
            }
            val matchesEvent = when (nextMessage.event) {
                "" -> {
//                    if (location.incoming.commands ?: defaults.commands
//                            && BridgeCommandRegistry.handleCommand(nextMessage)) return
                    location.incoming.plain ?: defaults.plain
                }
                ApiMessage.JOIN_LEAVE -> location.incoming.join_leave ?: defaults.join_leave
                ApiMessage.USER_ACTION -> location.incoming.action ?: defaults.action
                else -> {
                    logger.fatal("unknwon event type '${nextMessage.event}' on incoming message")
                    return
                }
            }

            if (!matchesEvent) {
                logger.info("location: $label dropped message '$nextMessage' event not enabled")
                continue
            }

            targetUUIDs.addAll(instance.collectPlayers(location.area))
        }

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

        targetUUIDs.forEach {
            //TODO: optimize send to all at once
            instance.wrappedSendToPlayer(it, message)
        }



//        if (nextMessage?.gateway == cfg.connect.gateway) {
//            if (!nextMessage.text.isBlank()) {
//                nextMessage.text = nextMessage.text.stripColorIn
//                val message = when (nextMessage.event) {
//                    "user_action" -> nextMessage.format(cfg.incoming.action)
//                    "" -> {
//                        // try to handle command and do not handle as a chat message
//                        if (BridgeCommandRegistry.handleCommand(nextMessage)) return
//                        nextMessage.format(cfg.incoming.chat)
//                    }
//                    "join_leave" -> nextMessage.format(cfg.incoming.joinPart)
//                    else -> {
//                        val user = nextMessage.username
//                        val text = nextMessage.text
//                        val json = nextMessage.encode()
//                        logger.debug("Threw out message with unhandled event: ${nextMessage.event}")
//                        logger.debug(" Message contents:")
//                        logger.debug(" User: $user")
//                        logger.debug(" Text: $text")
//                        logger.debug(" JSON: $json")
//                        return
//                    }
//                }
//                instance.wrappedSendToPlayers(message)
//            }
//        }
    }
}
