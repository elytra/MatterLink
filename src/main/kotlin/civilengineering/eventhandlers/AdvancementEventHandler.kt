package civilengineering.eventhandlers

import civilengineering.bridge.ApiMessage
import civilengineering.bridge.MessageHandler
import civilengineering.cfg
import civilengineering.util.antiping
import net.minecraftforge.event.entity.player.AdvancementEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AdvancementEventHandler {
    @SubscribeEvent
    fun handleAdvancements(event: AdvancementEvent) {
        if (cfg!!.relay.advancements && event.advancement.display != null) {
            val player = event.entityPlayer.name.antiping()
            val content = event.advancement.displayText.unformattedText
            MessageHandler.transmit(ApiMessage(
                    username = cfg!!.relay.systemUser,
                    text = "$player has earned the advancement $content"
            ))
        }
    }
}