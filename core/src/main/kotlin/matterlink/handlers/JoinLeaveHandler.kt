package matterlink.handlers

import matterlink.antiping
import matterlink.api.ApiMessage
import matterlink.api.ApiMessage.Companion.JOIN_LEAVE
import matterlink.bridge.MessageHandlerInst
import matterlink.config.cfg
import matterlink.mapFormat
import matterlink.stripColorOut

object JoinLeaveHandler {
    fun handleJoin(player: String) {
        if (cfg.outgoing.joinPart.enable) {
            val msg = cfg.outgoing.joinPart.joinServer.mapFormat(
                    mapOf(
                            "{username}" to player.stripColorOut,
                            "{username:antiping}" to player.stripColorOut.antiping
                    )
            )
            MessageHandlerInst.transmit(
                    ApiMessage(
                            _text = msg,
                            _event = JOIN_LEAVE
                    )
            )
        }
    }

    fun handleLeave(player: String) {
        if (cfg.outgoing.joinPart.enable) {
            val msg = cfg.outgoing.joinPart.partServer.mapFormat(
                    mapOf(
                            "{username}" to player.stripColorOut,
                            "{username:antiping}" to player.stripColorOut.antiping
                    )
            )
            MessageHandlerInst.transmit(
                    ApiMessage(
                            _text = msg,
                            _event = JOIN_LEAVE
                    )
            )
        }
    }
}