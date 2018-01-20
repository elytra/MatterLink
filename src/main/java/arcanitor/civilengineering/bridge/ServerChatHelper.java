package arcanitor.civilengineering.bridge;

import arcanitor.civilengineering.bridge.ApiMessage;
import arcanitor.civilengineering.bridge.MessageHandler;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ServerChatHelper {
    //public static ConcurrentLinkedQueue<ApiMessage> messages = new ConcurrentLinkedQueue();

    @SubscribeEvent
    public static void onServerUpdate(TickEvent.ServerTickEvent event) {
        ApiMessage nextMessage = MessageHandler.rcvQueue.poll();

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
