package matterlink

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.PlayerEvent
import cpw.mods.fml.common.gameevent.TickEvent
import kotlinx.coroutines.runBlocking
import matterlink.config.cfg
import matterlink.handlers.ChatEvent
import matterlink.handlers.ChatProcessor
import matterlink.handlers.DeathHandler
import matterlink.handlers.JoinLeaveHandler
import matterlink.handlers.ProgressHandler
import matterlink.handlers.TickHandler
import net.minecraft.command.server.CommandBroadcast
import net.minecraft.command.server.CommandEmote
import net.minecraft.entity.Entity
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
    fun progressEvent(e: AchievementEvent) = runBlocking {
        val achievement = e.achievement
        val entityPlayer = e.entityPlayer as? EntityPlayerMP ?: return@runBlocking
        val statFile = entityPlayer.statFile

        if (!statFile.canUnlockAchievement(achievement) || statFile.hasAchievementUnlocked(achievement)) {
            return@runBlocking
        }

        ProgressHandler.handleProgress(
            name = e.entityPlayer.displayName,
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
    fun chatEvent(e: ServerChatEvent) = runBlocking {
        if (e.isCanceled) return@runBlocking
        e.isCanceled = ChatProcessor.sendToBridge(
            user = e.player.displayName,
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
    fun commandEvent(e: CommandEvent) = runBlocking {
        val sender = when {
            e.sender is DedicatedServer -> cfg.outgoing.systemUser
            else -> e.sender.commandSenderName
        }
        val args = e.parameters.joinToString(" ")
        val type = with(e.command) {
            when {
                this is CommandEmote || commandName.equals("me", true) -> ChatEvent.ACTION
                this is CommandBroadcast || commandName.equals("say", true) -> ChatEvent.BROADCAST
                else -> return@runBlocking
            }
        }
        val s = e.sender
        val (x, y, z) = when (s) {
            is Entity -> Triple(s.posX.toInt(), s.posY.toInt(), s.posZ.toInt())
            else -> with(s.commandSenderPosition) { Triple(posX, posY, posZ) }
        }
        ChatProcessor.sendToBridge(
            user = sender,
            msg = args,
            event = type,
            x = x,
            y = y,
            z = z,
            dimension = when {
                e.sender is DedicatedServer -> null
                else -> e.sender.entityWorld.provider.dimensionId
            }
        )

    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun deathEvent(e: LivingDeathEvent) = runBlocking {
        if (e.entityLiving is EntityPlayer) {
            val player = e.entityLiving as EntityPlayer
            DeathHandler.handleDeath(
                player = player.displayName,
                deathMessage = e.entityLiving.combatTracker.func_151521_b().unformattedText,
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
    fun joinEvent(e: PlayerEvent.PlayerLoggedInEvent) = runBlocking {
        JoinLeaveHandler.handleJoin(
            player = e.player.displayName,
            x = e.player.posX.toInt(),
            y = e.player.posY.toInt(),
            z = e.player.posZ.toInt(),
            dimension = e.player.dimension
        )
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun leaveEvent(e: PlayerEvent.PlayerLoggedOutEvent) = runBlocking {
        JoinLeaveHandler.handleLeave(
            player = e.player.displayName,
            x = e.player.posX.toInt(),
            y = e.player.posY.toInt(),
            z = e.player.posZ.toInt(),
            dimension = e.player.dimension
        )
    }

    //FORGE-DEPENDENT
    @SubscribeEvent
    fun serverTickEvent(e: TickEvent.ServerTickEvent) = runBlocking {
        if (e.phase == TickEvent.Phase.END)
            TickHandler.handleTick()
    }
}