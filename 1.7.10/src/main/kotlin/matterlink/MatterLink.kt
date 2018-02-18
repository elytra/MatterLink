package matterlink

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.*
import matterlink.command.CommandMatterlink
import matterlink.bridge.command.IMinecraftCommandSender
import matterlink.command.MatterlinkCommandSender
import matterlink.config.cfg
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChatComponentText
import net.minecraftforge.common.MinecraftForge
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger

lateinit var logger: Logger

@Mod(
        modid = MODID,
        name = NAME, version = MODVERSION,
        useMetadata = true,
        acceptableRemoteVersions = "*"
)
class MatterLink : IMatterLink() {
    init {
        instance = this
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(EventHandler)
        FMLCommonHandler.instance().bus().register(EventHandler)

        logger = event.modLog
        logger.info("Building bridge!")

        cfg = MatterLinkConfig(event.modConfigurationDirectory)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        this.registerBridgeCommands()
    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        logger.debug("Registering server commands")
        event.registerServerCommand(CommandMatterlink())
        serverStartTime = System.currentTimeMillis()
        connect()
    }

    @Mod.EventHandler
    fun serverStopping(event: FMLServerStoppingEvent) {
        disconnect()
    }

    //FORGE-DEPENDENT
    override fun wrappedSendToPlayers(msg: String) {
        MinecraftServer.getServer().addChatMessage(ChatComponentText(msg));
    }

    //FORGE-DEPENDENT
    override fun wrappedPlayerList(): Array<String> {
        return MinecraftServer.getServer().allUsernames
    }

    override fun log(level: String, formatString: String, vararg data: Any) =
            logger.log(Level.toLevel(level, Level.INFO), formatString, *data)

    override var commandSender: IMinecraftCommandSender = MatterlinkCommandSender

    override val mcVersion: String = MCVERSION
    override val modVersion: String = MODVERSION
}
