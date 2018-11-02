package matterlink.command

import kotlinx.coroutines.runBlocking
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatComponentText


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

    override fun processCommand(sender: ICommandSender, args: Array<String>) = runBlocking {
        if (args.isEmpty()) {
            throw WrongUsageException("Invalid command! Valid uses: ${getCommandUsage(sender)}")
        }

        val uuid = (sender as? EntityPlayer)?.uniqueID?.toString()
        val reply = CommandCoreML.execute(args, sender.commandSenderName, uuid)

        if (reply.isNotEmpty()) {
            sender.addChatMessage(ChatComponentText(reply))
        }
    }
}
