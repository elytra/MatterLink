package matterlink.command

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.util.ChatComponentText


class CommandMatterlink : CommandBase() {
    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            throw WrongUsageException("Invalid command! Valid uses: ${this.getCommandUsage(sender)}")
        }

        val reply = CommandCore.execute(args)

        if (reply.isNotEmpty()) {
            sender.addChatMessage(ChatComponentText(reply))
        }
    }

    override fun getCommandName(): String {
        return CommandCore.getName()
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return CommandCore.getUsage()
    }

    override fun getCommandAliases(): List<String> {
        return CommandCore.getAliases()
    }
}
