package matterlink.handlers

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.cfg
import matterlink.antiping
import matterlink.bridge.JOIN_LEAVE
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent

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