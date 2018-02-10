package matterlink

import matterlink.bridge.ServerChatHandler
import matterlink.bridge.USER_ACTION
import matterlink.config.cfg
import matterlink.handlers.*
import net.minecraft.command.server.CommandBroadcast
import net.minecraft.command.server.CommandEmote
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.tileentity.TileEntityCommandBlock
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.AdvancementEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

//FORGE-DEPENDENT
@Mod.EventBusSubscriber
class EventWrapper {
    companion object JavaIsDumb {

        //MC-VERSION & FORGE DEPENDENT
        @SubscribeEvent
        @JvmStatic
        fun progressEvent(e: AdvancementEvent) {
            if (e.advancement.display == null) return
            val name = e.entityPlayer.name
            val text = "has made the advancement ${e.advancement.displayText.unformattedText}"
            ProgressHandler.handleProgress(name, text)
        }

        //FORGE-DEPENDENT
        @SubscribeEvent
        @JvmStatic
        fun chatEvent(e: ServerChatEvent) {
            val user = e.username
            val msg = e.message
            ChatHandler.handleChat(user, msg)
        }

        //FORGE-DEPENDENT
        @SubscribeEvent
        @JvmStatic
        fun commandEvent(e: CommandEvent) {
            val sender =
                    when {
                        e.sender is EntityPlayer -> e.sender.name
                        e.sender is DedicatedServer -> cfg!!.relay.systemUser
                        e.sender is TileEntityCommandBlock -> "CommandBlock"
                        else -> return
                    }

            if (e.command is CommandEmote || e.command is CommandBroadcast) {
                val args = e.parameters.joinToString(" ")
                val type =
                        when {
                            e.command is CommandEmote -> USER_ACTION
                            else -> ""
                        }
                CommandHandler.handleCommand(sender, args, type)
            }
        }

        //FORGE-DEPENDENT
        @SubscribeEvent
        @JvmStatic
        fun deathEvent(e: LivingDeathEvent) {
            if (e.entityLiving is EntityPlayer) {
                DeathHandler.handleDeath(
                        e.entityLiving.name,
                        e.entityLiving.combatTracker.deathMessage.unformattedText
                )
            }
        }

        //FORGE-DEPENDENT
        @SubscribeEvent
        @JvmStatic
        fun joinEvent(e: PlayerEvent.PlayerLoggedInEvent) {
            JoinLeaveHandler.handleJoin(e.player.name)
        }

        //FORGE-DEPENDENT
        @SubscribeEvent
        @JvmStatic
        fun leaveEvent(e: PlayerEvent.PlayerLoggedOutEvent) {
            JoinLeaveHandler.handleLeave(e.player.name)
        }

        //FORGE-DEPENDENT
        @SubscribeEvent
        @JvmStatic
        fun serverTickEvent(e: TickEvent.ServerTickEvent) {
            ServerChatHandler.writeIncomingToChat()
        }
    }
}