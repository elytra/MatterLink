package matterlink

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.PlayerEvent
import cpw.mods.fml.common.gameevent.TickEvent
import matterlink.bridge.ServerChatHandler
import matterlink.bridge.USER_ACTION
import matterlink.config.cfg
import matterlink.handlers.*
import net.minecraft.command.server.CommandBroadcast
import net.minecraft.command.server.CommandEmote
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.tileentity.TileEntityCommandBlock
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.AchievementEvent

//FORGE-DEPENDENT
object EventHandler {

    //MC-VERSION & FORGE DEPENDENT
    @SubscribeEvent
    fun progressEvent(e: AchievementEvent) {
        ProgressHandler.handleProgress(
                name = e.entityPlayer.displayName,
                message = "has earned the achievement",
                display = e.achievement.statName.unformattedText
        )
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun chatEvent(e: ServerChatEvent) {
        ChatProcessor.sendToBridge(
                user = e.username,
                msg = e.message,
                event = ""
        )
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun commandEvent(e: CommandEvent) {
        val sender = when {
            e.sender is EntityPlayer -> e.sender.commandSenderName
            e.sender is DedicatedServer -> cfg.relay.systemUser
            e.sender is TileEntityCommandBlock -> "CommandBlock"
            else -> return
        }
        val args = e.parameters.joinToString(" ")
        val type = when {
            e.command is CommandEmote -> USER_ACTION
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
        ServerChatHandler.writeIncomingToChat(MinecraftServer.getServer().tickCounter)
    }
}