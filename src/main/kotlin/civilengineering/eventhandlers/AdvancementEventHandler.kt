package civilengineering.eventhandlers

import civilengineering.CivilEngineering
import civilengineering.CivilEngineeringConfig
import civilengineering.Util
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
            var player: String = Util.antiping(event.entityPlayer.name)

            MessageHandler.transmit(ApiMessage("Server",player+" has earned the advancement "+event.advancement.displayText.unformattedText))
        }
    }
}