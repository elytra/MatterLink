package civilengineering.eventhandlers

import civilengineering.Config
import civilengineering.bridge.ApiMessage
import civilengineering.bridge.MessageHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class DeathEventHandler {
    @SubscribeEvent
    fun handleLivingDeathEvent(event: LivingDeathEvent) {
        if (Config.relayDeathEvents) {
            val entity = event.entityLiving
            if (entity is EntityPlayer) {
                val message = entity.getCombatTracker().deathMessage.unformattedText
                MessageHandler.transmit(ApiMessage("Server", message))
            }
        }
    }
}
