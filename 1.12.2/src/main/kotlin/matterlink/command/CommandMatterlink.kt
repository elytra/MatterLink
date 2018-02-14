package matterlink.command

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString


class CommandMatterlink : CommandBase() {
    override fun getName(): String {
        return CommandCore.getName()
    }

    override fun getUsage(sender: ICommandSender): String {
        return CommandCore.getUsage()
    }

    override fun getAliases(): List<String> {
        return CommandCore.getAliases()
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            throw WrongUsageException("Invalid command! Valid uses: ${this.getUsage(sender)}")
        }
        //TODO: check if sender is OP or test if normal users cannot send this

        val reply = CommandCore.execute(args)

        if (reply.isNotEmpty() && sender.sendCommandFeedback()) {
            sender.sendMessage(TextComponentString(reply))
        }
    }

}
