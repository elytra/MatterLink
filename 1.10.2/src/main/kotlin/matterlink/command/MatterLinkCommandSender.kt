package matterlink.command

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.bridge.command.IMinecraftCommandSender
import matterlink.config.cfg
import net.minecraft.command.CommandResultStats
import net.minecraft.command.ICommandSender
import net.minecraft.entity.Entity
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler
import javax.annotation.Nonnull

class MatterLinkCommandSender(user: String, userId: String, server: String) : IMinecraftCommandSender(user, userId, server), ICommandSender {
    private var level: Int = 0

    override fun execute(cmdString: String): Boolean {
        return 0 < FMLCommonHandler.instance().minecraftServerInstance.commandManager.executeCommand(
                this,
                cmdString
        )
    }

    override fun getDisplayName(): ITextComponent {
        return TextComponentString(user)
    }

    override fun getName() = accountName

    override fun getEntityWorld(): World {
        return FMLCommonHandler.instance().minecraftServerInstance.worldServerForDimension(0)
    }

    override fun canUseCommand(permLevel: Int, commandName: String): Boolean {
        //we check user on our end
        return canExecute(commandName)
    }

    override fun getServer(): MinecraftServer? {
        return FMLCommonHandler.instance().minecraftServerInstance
    }

    override fun sendMessage(@Nonnull component: ITextComponent?) {
        MessageHandler.transmit(ApiMessage(
                username = cfg.relay.systemUser,
                text = component!!.unformattedComponentText
        ))

    }

    override fun sendCommandFeedback(): Boolean {
        return true
    }

    override fun getPosition(): BlockPos = BlockPos.ORIGIN

    override fun setCommandStat(type: CommandResultStats.Type?, amount: Int) {}

    override fun getPositionVector(): Vec3d = Vec3d.ZERO

    override fun getCommandSenderEntity(): Entity? = null
}