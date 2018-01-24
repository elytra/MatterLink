package civilengineering.eventhandlers

import civilengineering.CivilEngineering
import civilengineering.bridge.ApiMessage
import civilengineering.bridge.MessageHandler
import net.minecraft.command.server.CommandBroadcast
import net.minecraft.command.server.CommandEmote
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class CommandEventHandler {
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

            MessageHandler.transmit(ApiMessage(username=user, text=message, event="user_action"))
        } else if(event.command is CommandBroadcast) {
            var message = ""
            for (word in event.parameters) {
                message = message + " " + word
            }
            message = message.trim { it <= ' ' }
            val name: String = if(event.sender.name.equals("@")) "CommandBlock" else event.sender.name

            MessageHandler.transmit(ApiMessage(
                    username = name,
                    text = message
            ))
        }
    }
}
