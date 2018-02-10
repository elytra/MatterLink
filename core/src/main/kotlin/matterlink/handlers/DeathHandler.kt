package matterlink.handlers

import matterlink.antiping
import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.config.cfg

object DeathHandler {
    fun handleDeath(player: String, deathMessage: String) {
        if (cfg!!.relay.deathEvents) {
            val msg = deathMessage.replace(player, player.antiping())
            MessageHandler.transmit(ApiMessage(
                    username = cfg!!.relay.systemUser,
                    text = msg
            ))
        }
    }
}
