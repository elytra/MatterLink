package arcanitor.civilengineering.eventhandlers;

import arcanitor.civilengineering.CivilEngineering;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class HandleChatMessage {
    @SubscribeEvent
    public static void handleServerChatEvent (ServerChatEvent event) {
        CivilEngineering.logger.info("Message on server: "+event.getMessage()+" sent by "+event.getUsername());
    }
}
