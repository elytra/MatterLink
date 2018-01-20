package arcanitor.civilengineering.eventhandlers;

import arcanitor.civilengineering.Config;
import arcanitor.civilengineering.bridge.ApiMessage;
import arcanitor.civilengineering.bridge.MessageHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class DeathEventHandler {
    @SubscribeEvent
    public static void handleLivingDeathEvent (LivingDeathEvent event) {
        if(Config.relayDeathEvents) {
            EntityLivingBase entity = event.getEntityLiving();
            if (entity instanceof EntityPlayer) {
                String message = entity.getCombatTracker().getDeathMessage().getUnformattedText();
                MessageHandler.xmitQueue.add(new ApiMessage("Server",message));
            }
        }
    }
}
