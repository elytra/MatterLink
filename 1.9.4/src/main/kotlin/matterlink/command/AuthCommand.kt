package matterlink.command

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString


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

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            throw WrongUsageException("Invalid command! Valid uses: ${this.getCommandUsage(sender)}")
        }

        val uuid = (sender as? EntityPlayer)?.uniqueID?.toString()
        val reply = CommandCoreAuth.execute(args, sender.name, uuid)

        if (reply.isNotEmpty() && sender.sendCommandFeedback()) {
            sender.addChatMessage(TextComponentString(reply))
        }
    }

}
