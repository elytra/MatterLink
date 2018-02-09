package matterlink.handlers

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.cfg
import matterlink.antiping
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

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
