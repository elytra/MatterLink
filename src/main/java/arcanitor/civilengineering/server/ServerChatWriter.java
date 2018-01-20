package arcanitor.civilengineering.server;

import arcanitor.civilengineering.CivilEngineering;
import arcanitor.civilengineering.bridge.ApiMessage;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerChatWriter {
    public static ConcurrentLinkedQueue<ApiMessage> messages = new ConcurrentLinkedQueue();

    @SubscribeEvent
    public static void onServerUpdate(TickEvent.ServerTickEvent event) {
        ApiMessage nextMessage = messages.poll();

        if (nextMessage != null) {
            String user = nextMessage.getUsername();
            String text = nextMessage.getMessage().trim();

            String message;

            if (!text.isEmpty()) {
                if (nextMessage.getEvent().equals("user_action")) {
                    message = "* " + user + " " + text;
                } else {
                    message = "<" + user + "> " + text;
                }
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(new TextComponentString(message));
            }
        }
    }
}
