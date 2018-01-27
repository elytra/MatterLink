package matterlink.eventhandlers

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.cfg
import matterlink.antiping
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent

class JoinLeaveHandler {
    @SubscribeEvent
    fun handleJoinEvent(event: PlayerEvent.PlayerLoggedInEvent) {
        if (cfg!!.relay.joinLeave) {
            val player: String = event.player.name.antiping()
            MessageHandler.transmit(ApiMessage(
                    username = cfg!!.relay.systemUser,
                    text = "$player has connected to the server.",
                    event = "join_leave"
            ))
        }
    }

    @SubscribeEvent
    fun handleLeaveEvent(event: PlayerEvent.PlayerLoggedOutEvent) {
        if (cfg!!.relay.joinLeave) {
            val player = event.player.name.antiping()
            MessageHandler.transmit(ApiMessage(
                    username = cfg!!.relay.systemUser,
                    text = "$player has disconnected from the server.",
                    event = "join_leave"
            ))
        }
    }
}