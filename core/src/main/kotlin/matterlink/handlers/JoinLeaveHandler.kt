package matterlink.handlers

import matterlink.antiping
import matterlink.api.ApiMessage
import matterlink.api.ApiMessage.Companion.JOIN_LEAVE
import matterlink.bridge.MessageHandlerInst
import matterlink.config.cfg
import matterlink.mapFormat
import matterlink.stripColorOut

object JoinLeaveHandler {
    fun handleJoin(player: String,
                   x: Int, y: Int, z: Int,
                   dimension: Int) {
        if (cfg.outgoing.joinPart.enable) {
            val msg = cfg.outgoing.joinPart.joinServer.mapFormat(
                    mapOf(
                            "{username}" to player.stripColorOut,
                            "{username:antiping}" to player.stripColorOut.antiping
                    )
            )
            LocationHandler.sendToLocations(
                    msg = msg,
                    x = x, y = y, z = z, dimension = dimension,
                    event = ChatEvent.JOIN,
                    systemuser = true,
                    cause = "$player joined"
            )
        }
    }

    fun handleLeave(player: String,
                    x: Int, y: Int, z: Int,
                    dimension: Int) {
        if (cfg.outgoing.joinPart.enable) {
            val msg = cfg.outgoing.joinPart.partServer.mapFormat(
                    mapOf(
                            "{username}" to player.stripColorOut,
                            "{username:antiping}" to player.stripColorOut.antiping
                    )
            )
            LocationHandler.sendToLocations(
                    msg = msg,
                    x = x, y = y, z = z, dimension = dimension,
                    event = ChatEvent.JOIN,
                    systemuser = true,
                    cause = "$player left"
            )
        }
    }
}