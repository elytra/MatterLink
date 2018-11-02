package matterlink.handlers

import matterlink.antiping
import matterlink.config.cfg
import matterlink.stripColorOut
import java.util.Random

object DeathHandler {
    private val random = Random()

    suspend fun handleDeath(
        player: String,
        deathMessage: String,
        damageType: String,
        x: Int, y: Int, z: Int,
        dimension: Int
    ) {
        if (cfg.outgoing.death.enable) {
            var msg = deathMessage.stripColorOut.replace(player, player.stripColorOut.antiping)
            if (cfg.outgoing.death.damageType) {
                val emojis = cfg.outgoing.death.damageTypeMapping[damageType]
                    ?: arrayOf("\uD83D\uDC7B unknown type '$damageType'")
                val damageEmoji = emojis[random.nextInt(emojis.size)]
                msg += " $damageEmoji"
            }
            LocationHandler.sendToLocations(
                msg = msg,
                x = x, y = y, z = z, dimension = dimension,
                event = ChatEvent.DEATH,
                cause = "Death Event of $player",
                systemuser = true
            )
        }
    }
}
