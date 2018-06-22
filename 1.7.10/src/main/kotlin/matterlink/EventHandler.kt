package matterlink

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.PlayerEvent
import cpw.mods.fml.common.gameevent.TickEvent
import matterlink.api.ApiMessage.Companion.USER_ACTION
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
                name = e.entityPlayer.displayName,
                message = "has earned the achievement",
                display = e.achievement.statName.unformattedText
        )
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun chatEvent(e: ServerChatEvent) {
        if(e.isCanceled) return
        ChatProcessor.sendToBridge(
                user = e.player.displayName,
                msg = e.message,
                event = ""
        )
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun commandEvent(e: CommandEvent) {
        val sender = when {
            e.sender is DedicatedServer -> cfg.outgoing.systemUser
            else -> e.sender.commandSenderName
        }
        val args = e.parameters.joinToString(" ")
        val type = when {
            e.command is CommandEmote -> USER_ACTION
            e.command.commandName == "me" -> USER_ACTION
            e.command is CommandBroadcast -> ""
            else -> return
        }
        ChatProcessor.sendToBridge(user = sender, msg = args, event = type)

    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun deathEvent(e: LivingDeathEvent) {
        if (e.entityLiving is EntityPlayer) {
            val player = e.entityLiving as EntityPlayer
            DeathHandler.handleDeath(
                    player = player.displayName,
                    deathMessage = e.entityLiving.combatTracker.func_151521_b().unformattedText,
                    damageType = e.source.damageType
            )
        }
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun joinEvent(e: PlayerEvent.PlayerLoggedInEvent) {
        JoinLeaveHandler.handleJoin(e.player.displayName)
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun leaveEvent(e: PlayerEvent.PlayerLoggedOutEvent) {
        JoinLeaveHandler.handleLeave(e.player.displayName)
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun serverTickEvent(e: TickEvent.ServerTickEvent) {
        if (e.phase == TickEvent.Phase.END)
            TickHandler.handleTick()
    }
}