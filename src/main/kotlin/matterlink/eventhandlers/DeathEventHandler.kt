package matterlink.eventhandlers

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.cfg
import matterlink.antiping
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class DeathEventHandler {
    @SubscribeEvent
    fun handleLivingDeathEvent(event: LivingDeathEvent) {
        if (cfg!!.relay.deathEvents) {
            val entity = event.entityLiving
            if (entity is EntityPlayer) {
                val message = entity.getCombatTracker().deathMessage.unformattedText
                        .replace(entity.name, entity.name.antiping())
                MessageHandler.transmit(ApiMessage(
                        username = cfg!!.relay.systemUser,
                        text = message
                ))
            }
        }
    }
}
