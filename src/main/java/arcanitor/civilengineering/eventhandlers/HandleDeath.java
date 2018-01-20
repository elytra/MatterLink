package arcanitor.civilengineering.eventhandlers;

import arcanitor.civilengineering.CivilEngineering;
import arcanitor.civilengineering.Config;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class HandleDeath {
    @SubscribeEvent
    public static void handleLivingDeathEvent (LivingDeathEvent event) {
        if(Config.relayDeathEvents) {
            EntityLivingBase entity = event.getEntityLiving();
            if (entity instanceof EntityPlayer) {
                String message = entity.getCombatTracker().getDeathMessage().getUnformattedText();
                CivilEngineering.logger.info(message);
            }
        }

    }


}
