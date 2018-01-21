package civilengineering.eventhandlers

import civilengineering.bridge.ApiMessage
import civilengineering.bridge.MessageHandler
import net.minecraft.command.server.CommandEmote
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class UserActionHandler {
    @SubscribeEvent
    fun handleCommandEvent(event: CommandEvent) {
        if (event.command is CommandEmote && event.sender is EntityPlayer) {
            val args = event.parameters

            val user = event.sender.name
            var message = ""

            for (word in args) {
                message = message + " " + word
            }
            message = message.trim { it <= ' ' }

            MessageHandler.transmit(ApiMessage(user, message, "user_action"))
        }
    }
}
