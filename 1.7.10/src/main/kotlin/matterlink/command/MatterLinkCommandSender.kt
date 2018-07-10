package matterlink.command

import matterlink.bridge.command.IBridgeCommand
import matterlink.bridge.command.IMinecraftCommandSender
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChunkCoordinates
import net.minecraft.util.IChatComponent
import net.minecraft.world.World

class MatterLinkCommandSender(
        user: String,
        env: IBridgeCommand.CommandEnvironment,
        op: Boolean) : IMinecraftCommandSender(user, env, op), ICommandSender {

    override fun execute(cmdString: String): Boolean {
        return 0 < MinecraftServer.getServer().commandManager.executeCommand(
                this,
                cmdString
        ).apply {
            sendReply(cmdString)
        }
    }

    override fun getFormattedCommandSenderName(): IChatComponent {
        return ChatComponentText(displayName)
    }

    override fun getCommandSenderName() = accountName

    override fun getEntityWorld(): World {
        return MinecraftServer.getServer().worldServerForDimension(0)
    }

    override fun canCommandSenderUseCommand(permLevel: Int, commandName: String): Boolean {
        //we do permission
        return canExecute(commandName)
    }

    override fun addChatMessage(component: IChatComponent) {
        appendReply(component.unformattedText)
    }

    override fun getCommandSenderPosition(): ChunkCoordinates = ChunkCoordinates(0, 0, 0)
}