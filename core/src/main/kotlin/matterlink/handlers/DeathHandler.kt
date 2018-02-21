package matterlink.handlers

import matterlink.antiping
import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.config.cfg
import java.util.*

object DeathHandler {
    private val random = Random()

    fun handleDeath(
            player: String,
            deathMessage: String,
            damageType: String
    ) {
        if (cfg.outgoing.death.enable) {
            var msg = deathMessage.replace(player, player.antiping)
            if (cfg.outgoing.death.damageType) {
                val emojis = cfg.outgoing.death.damageTypeMapping[damageType]?.split(' ')
                        ?: listOf("\uD83D\uDC7B unknown type '$damageType'")
                val damageEmoji = emojis[random.nextInt(emojis.size)]
                msg += " " + damageEmoji
            }
            MessageHandler.transmit(ApiMessage(
                    text = msg
            ))
        }
    }
}
