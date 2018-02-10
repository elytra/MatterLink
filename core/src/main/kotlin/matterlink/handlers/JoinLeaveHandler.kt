package matterlink.handlers

import matterlink.antiping
import matterlink.bridge.ApiMessage
import matterlink.bridge.JOIN_LEAVE
import matterlink.bridge.MessageHandler
import matterlink.config.cfg
import matterlink.mapFormat

object JoinLeaveHandler {
    fun handleJoin(player: String) {
        if (cfg!!.relay.joinLeave) {
            val msg = cfg!!.formattingJoinLeave.joinServer.mapFormat(
                    mapOf(
                            "{username}" to player,
                            "{username:antiping}" to player.antiping()
                    )
            )
            MessageHandler.transmit(ApiMessage(
                    username = cfg!!.relay.systemUser,
                    text = msg,
                    event = JOIN_LEAVE
            ))
        }
    }

    fun handleLeave(player: String) {
        if (cfg!!.relay.joinLeave) {
            val msg = cfg!!.formattingJoinLeave.leaveServer.mapFormat(
                    mapOf(
                            "{username}" to player,
                            "{username:antiping}" to player.antiping()
                    )
            )
            MessageHandler.transmit(ApiMessage(
                    username = cfg!!.relay.systemUser,
                    text = msg,
                    event = JOIN_LEAVE
            ))
        }
    }
}