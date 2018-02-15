package matterlink.command

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
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

object MatterlinkCommandSender : IMinecraftCommandSender, ICommandSender {
    private var level: Int = 0

    override fun execute(cmdString: String, level: Int): Boolean {
        return 0 < FMLCommonHandler.instance().minecraftServerInstance.commandManager.executeCommand(
                this,
                cmdString
        )
    }

    override fun getDisplayName(): ITextComponent {
        return TextComponentString("MatterLink")
    }

    override fun getName(): String {
        return "MatterLink"
    }

    override fun getEntityWorld(): World {
        return FMLCommonHandler.instance().minecraftServerInstance.getWorld(0)
    }

    override fun canUseCommand(permLevel: Int, commandName: String?): Boolean {
        //TODO: Implement actual permissions checking
        return true
    }

    override fun getServer(): MinecraftServer? {
        return FMLCommonHandler.instance().minecraftServerInstance
    }

    override fun sendMessage(@Nonnull component: ITextComponent?) {
        MessageHandler.transmit(ApiMessage(
                username = cfg!!.relay.systemUser,
                text = component!!.unformattedComponentText
        ))

    }

    override fun sendCommandFeedback(): Boolean {
        return true
    }

    //WtfMojangWhy
    override fun getPosition(): BlockPos = BlockPos.ORIGIN

    override fun setCommandStat(type: CommandResultStats.Type?, amount: Int) {}

    override fun getPositionVector(): Vec3d = Vec3d.ZERO

    override fun getCommandSenderEntity(): Entity? = null
}