package matterlink.command

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString


object MatterLinkCommand : CommandBase() {
    override fun getCommandName(): String {
        return CommandCoreML.name
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return CommandCoreML.usage
    }

    override fun getCommandAliases(): List<String> {
        return CommandCoreML.aliases
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            throw WrongUsageException("Invalid command! Valid uses: ${this.getCommandUsage(sender)}")
        }

        val uuid = (sender as? EntityPlayer)?.uniqueID?.toString()
        val reply = CommandCoreML.execute(args, sender.name, uuid)

        if (reply.isNotEmpty() && sender.sendCommandFeedback()) {
            sender.addChatMessage(TextComponentString(reply))
        }
    }
}
