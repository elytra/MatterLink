package arcanitor.civilengineering.eventhandlers;

import arcanitor.civilengineering.CivilEngineering;
import arcanitor.civilengineering.bridge.ApiMessage;
import arcanitor.civilengineering.bridge.OutgoingMessageHandler;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ChatMessageHandler {
    @SubscribeEvent
    public static void handleServerChatEvent (ServerChatEvent event) {
        String message = event.getMessage().trim();
        if (!message.isEmpty())
            OutgoingMessageHandler.queue.add(new ApiMessage(event.getUsername(),message));
    }
}
