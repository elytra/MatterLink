package matterlink.command

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.config.cfg
import net.minecraft.command.ICommandSender
import net.minecraft.entity.Entity
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChunkCoordinates
import net.minecraft.util.IChatComponent
import net.minecraft.world.World

object MatterlinkCommandSender : IMinecraftCommandSender, ICommandSender {

    private var level: Int = 0

    override fun execute(cmdString: String, level: Int): Boolean {
        return 0 < MinecraftServer.getServer().commandManager.executeCommand(
                this,
                cmdString
        )
    }

    override fun getFormattedCommandSenderName(): IChatComponent {
        return ChatComponentText(commandSenderName)
    }
    
    override fun getCommandSenderName(): String {
        return "MatterLink"
    }

    override fun getEntityWorld(): World {
        return MinecraftServer.getServer().worldServerForDimension(0)
    }

    override fun canCommandSenderUseCommand(permLevel: Int, commandName: String?): Boolean {
        //TODO: Implement actual permissions checking
        return true
    }

    override fun addChatMessage(component: IChatComponent) {
        MessageHandler.transmit(ApiMessage(
                username = cfg.relay.systemUser,
                text = component.unformattedText
        ))
    }

    override fun getCommandSenderPosition(): ChunkCoordinates = ChunkCoordinates(0,0,0)
}