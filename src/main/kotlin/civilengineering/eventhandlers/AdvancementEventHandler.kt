package civilengineering.eventhandlers

import civilengineering.CivilEngineering
import civilengineering.CivilEngineeringConfig
import civilengineering.bridge.ApiMessage
import civilengineering.bridge.MessageHandler
import civilengineering.cfg
import net.minecraftforge.event.entity.player.AdvancementEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AdvancementEventHandler {
    @SubscribeEvent
    fun handleAdvancements(event: AdvancementEvent) {
        if(cfg!!.relay.advancements && event.advancement.display != null) {
            val zwsp: Char = '\u200B'
            var player: String = event.entityPlayer.name
            player = player[0].toString()+zwsp+player.substring(1) //antiping
            //toString is necessary, otherwise the JVM thinks we're trying to do integer addition
            MessageHandler.transmit(ApiMessage("Server",player+" has earned the advancement "+event.advancement.displayText.unformattedText))
        }
    }
}