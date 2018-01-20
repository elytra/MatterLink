package arcanitor.civilengineering.eventhandlers;

import arcanitor.civilengineering.bridge.ApiMessage;
import arcanitor.civilengineering.bridge.MessageHandler;
import net.minecraft.command.server.CommandEmote;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class UserActionHandler {
    @SubscribeEvent
    public static void handleCommandEvent(CommandEvent event) {
        if(event.getCommand() instanceof CommandEmote && event.getSender() instanceof EntityPlayer) {
            String[] args = event.getParameters();

            String user = event.getSender().getName();
            String message = "";

            for(String word:args) {
                message = message + " " + word;
            }
            message = message.trim();

            MessageHandler.xmitQueue.add(new ApiMessage(user,message,"user_action"));
        }
    }
}