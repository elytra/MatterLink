package matterlink.handlers

import matterlink.antiping
import matterlink.bridge.ApiMessage
import matterlink.bridge.JOIN_LEAVE
import matterlink.bridge.MessageHandler
import matterlink.cfg

object JoinLeaveHandler {
    fun handleJoin(player: String) {
        if (cfg!!.relay.joinLeave) {
            val user = player.antiping()
            MessageHandler.transmit(ApiMessage(
                    username = cfg!!.relay.systemUser,
                    text = "$user has connected to the server.",
                    event = JOIN_LEAVE
            ))
        }
    }

    fun handleLeave(player: String) {
        if (cfg!!.relay.joinLeave) {
            val user = player.antiping()
            MessageHandler.transmit(ApiMessage(
                    username = cfg!!.relay.systemUser,
                    text = "$user has disconnected from the server.",
                    event = JOIN_LEAVE
            ))
        }
    }
}