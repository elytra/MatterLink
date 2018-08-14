package matterlink.handlers

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.config.cfg
import matterlink.logger
import matterlink.stripColorOut
import java.util.*


enum class ChatEvent {
    PLAIN, ACTION, DEATH, JOIN, LEAVE, ADVANCEMENT, BROADCAST, STATUS
}

object LocationHandler {

    fun sendToLocations(
            user: String = cfg.outgoing.systemUser,
            msg: String,
            x: Int, y: Int, z: Int,
            dimension: Int?,
            event: ChatEvent,
            systemuser: Boolean = false,
            uuid: UUID? = null,
            cause: String
    ): Boolean {
        val defaults = cfg.outgoingDefaults
        var handled = false
        val skips = mutableSetOf<String>()
        logger.info("locations: ${cfg.locations.map { it.label  }}")
        for (location in cfg.locations) {
            val label = location.label
            if(skips.contains(label)) {
                logger.info("skipping $label (contained in in $skips)")
                continue
            }
            if(!location.area.testForDim(dimension)) {
                logger.info("location: $label dropped message '$msg' from $user due to mismatched dimension")
                continue
        }
            if(!location.area.testInBounds(x, y, z)) {
                logger.info("location: $label dropped message '$msg' from $user out of coordinate bounds")
                continue
            }
            val matchesEvent = when (event) {
                ChatEvent.PLAIN -> location.outgoing.plain ?: defaults.plain
                ChatEvent.ACTION -> location.outgoing.action ?: defaults.action
                ChatEvent.DEATH -> location.outgoing.death ?: defaults.death
                ChatEvent.JOIN -> location.outgoing.join ?: defaults.join
                ChatEvent.LEAVE -> location.outgoing.leave ?: defaults.leave
                ChatEvent.ADVANCEMENT -> location.outgoing.advancement ?: defaults.advancement
                ChatEvent.BROADCAST -> location.outgoing.broadcast ?: defaults.broadcast
                ChatEvent.STATUS -> location.outgoing.status ?: defaults.status
            }

            if (!matchesEvent) {
                logger.info("location: $label dropped message '$msg' from user: '$user', event not enabled")
                continue
            }

            skips.addAll(location.outgoing.skip)

            val eventStr = when (event) {
                ChatEvent.PLAIN -> ""
                ChatEvent.ACTION -> ApiMessage.USER_ACTION
                ChatEvent.DEATH -> ""
                ChatEvent.JOIN -> ApiMessage.JOIN_LEAVE
                ChatEvent.LEAVE -> ApiMessage.JOIN_LEAVE
                ChatEvent.ADVANCEMENT -> ""
                ChatEvent.BROADCAST -> ""
                ChatEvent.STATUS -> ""
            }

            val username = when {
                systemuser -> cfg.outgoing.systemUser
                else -> user
            }
            val avatar = when {
                systemuser ->
                    cfg.outgoing.avatar.systemUserAvatar
                cfg.outgoing.avatar.enable && uuid != null ->
                    cfg.outgoing.avatar.urlTemplate.replace("{uuid}", uuid.toString())
                else ->
                    null
            }
            when {
                msg.isNotBlank() -> MessageHandlerInst.transmit(
                        ApiMessage(
                                username = username.stripColorOut,
                                text = msg.stripColorOut,
                                event = eventStr,
                                gateway = location.gateway
                        ).apply {
                            avatar?.let {
                                this.avatar = it
                            }
                        },
                        cause = cause
                )
                else -> logger.warn("WARN: dropped blank message by '$user'")
            }
            logger.info("sent message through location: $label, cause: $cause")
            handled = true
        }
        return handled
    }
}