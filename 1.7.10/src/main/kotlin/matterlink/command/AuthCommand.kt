package matterlink.command

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatComponentText


object AuthCommand : CommandBase() {
    override fun getCommandName(): String {
        return CommandCoreAuth.name
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return CommandCoreAuth.usage
    }

    override fun getCommandAliases(): List<String> {
        return CommandCoreAuth.aliases
    }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            throw WrongUsageException("Invalid command! Valid uses: ${this.getCommandUsage(sender)}")
        }

        val uuid = (sender as? EntityPlayer)?.uniqueID?.toString()
        val reply = CommandCoreAuth.execute(args, sender.commandSenderName, uuid)

        if (reply.isNotEmpty()) {
            sender.addChatMessage(ChatComponentText(reply))
        }
    }
}
