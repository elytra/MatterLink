package matterlink

import matterlink.config.cfg
import matterlink.handlers.*
import net.minecraft.command.server.CommandBroadcast
import net.minecraft.command.server.CommandEmote
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.AchievementEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

//FORGE-DEPENDENT
object EventHandler {

    //MC-VERSION & FORGE DEPENDENT
    @SubscribeEvent
    fun progressEvent(e: AchievementEvent) {
        val achievement = e.achievement
        val entityPlayer = e.entityPlayer as? EntityPlayerMP ?: return
        val statFile = entityPlayer.statFile

        if (!statFile.canUnlockAchievement(achievement) || statFile.hasAchievementUnlocked(achievement)) {
            return
        }
        ProgressHandler.handleProgress(
                name = e.entityPlayer.displayName.unformattedText,
                message = "has earned the achievement",
                display = e.achievement.statName.unformattedText,
                x = e.entityPlayer.posX.toInt(),
                y = e.entityPlayer.posY.toInt(),
                z = e.entityPlayer.posZ.toInt(),
                dimension = e.entityPlayer.dimension
        )
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun chatEvent(e: ServerChatEvent) {
        if(e.isCanceled) return
        e.isCanceled = ChatProcessor.sendToBridge(
                user = e.player.displayName.unformattedText,
                msg = e.message,
                x = e.player.posX.toInt(),
                y = e.player.posY.toInt(),
                z = e.player.posZ.toInt(),
                dimension = e.player.dimension,
                event = ChatEvent.PLAIN,
                uuid = e.player.gameProfile.id
        )
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun commandEvent(e: CommandEvent) {
        val sender = when {
            e.sender is DedicatedServer -> cfg.outgoing.systemUser
            else -> e.sender.displayName.unformattedText
        }
        val args = e.parameters.joinToString(" ")
        val type = with(e.command) {
            when {
                this is CommandEmote || commandName.equals("me", true) -> ChatEvent.ACTION
                this is CommandBroadcast || commandName.equals("say", true) -> ChatEvent.BROADCAST
                else -> return
            }
        }
        ChatProcessor.sendToBridge(
                user = sender,
                msg = args,
                event = type,
                x = e.sender.position.x,
                y = e.sender.position.y,
                z = e.sender.position.z,
                dimension = when {
                    e.sender is DedicatedServer -> null
                    else -> e.sender.commandSenderEntity?.dimension ?: e.sender.entityWorld.provider.dimension
                }
        )
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun deathEvent(e: LivingDeathEvent) {
        if (e.entityLiving is EntityPlayer) {
            DeathHandler.handleDeath(
                    player = e.entityLiving.displayName.unformattedText,
                    deathMessage = e.entityLiving.combatTracker.deathMessage.unformattedText,
                    damageType = e.source.damageType,
                    x = e.entityLiving.posX.toInt(),
                    y = e.entityLiving.posY.toInt(),
                    z = e.entityLiving.posZ.toInt(),
                    dimension = e.entityLiving.dimension
            )
        }
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun joinEvent(e: PlayerEvent.PlayerLoggedInEvent) {
        JoinLeaveHandler.handleJoin(
                player = e.player.displayName.unformattedText,
                x = e.player.posX.toInt(),
                y = e.player.posY.toInt(),
                z = e.player.posZ.toInt(),
                dimension = e.player.dimension
        )
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun leaveEvent(e: PlayerEvent.PlayerLoggedOutEvent) {
        JoinLeaveHandler.handleLeave(
                player = e.player.displayName.unformattedText,
                x = e.player.posX.toInt(),
                y = e.player.posY.toInt(),
                z = e.player.posZ.toInt(),
                dimension = e.player.dimension
        )
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun serverTickEvent(e: TickEvent.ServerTickEvent) {
        if (e.phase == TickEvent.Phase.END)
            TickHandler.handleTick()
    }
}