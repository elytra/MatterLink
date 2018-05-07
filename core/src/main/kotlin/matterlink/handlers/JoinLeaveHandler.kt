package matterlink.handlers

import matterlink.antiping
import matterlink.api.ApiMessage
import matterlink.api.ApiMessage.JOIN_LEAVE
import matterlink.bridge.MessageHandlerInst
import matterlink.config.cfg
import matterlink.mapFormat

object JoinLeaveHandler {
    fun handleJoin(player: String) {
        if (cfg.outgoing.joinPart.enable) {
            val msg = cfg.outgoing.joinPart.joinServer.mapFormat(
                    mapOf(
                            "{username}" to player,
                            "{username:antiping}" to player.antiping
                    )
            )
            MessageHandlerInst.transmit(ApiMessage()
                    .setText(msg)
                    .setEvent(ApiMessage.JOIN_LEAVE)
            )
        }
    }

    fun handleLeave(player: String) {
        if (cfg.outgoing.joinPart.enable) {
            val msg = cfg.outgoing.joinPart.partServer.mapFormat(
                    mapOf(
                            "{username}" to player,
                            "{username:antiping}" to player.antiping
                    )
            )
            MessageHandlerInst.transmit(ApiMessage()
                    .setText(msg)
                    .setEvent(JOIN_LEAVE)
            )
        }
    }
}