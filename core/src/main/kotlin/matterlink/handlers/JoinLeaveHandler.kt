package matterlink.handlers

import matterlink.antiping
import matterlink.bridge.ApiMessage
import matterlink.bridge.JOIN_LEAVE
import matterlink.bridge.MessageHandler
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
            MessageHandler.transmit(ApiMessage(
                    text = msg,
                    event = JOIN_LEAVE
            ))
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
            MessageHandler.transmit(ApiMessage(
                    text = msg,
                    event = JOIN_LEAVE
            ))
        }
    }
}