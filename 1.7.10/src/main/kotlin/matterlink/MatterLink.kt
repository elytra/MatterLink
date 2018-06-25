package matterlink

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.event.FMLServerStartingEvent
import cpw.mods.fml.common.event.FMLServerStoppingEvent
import matterlink.command.CommandMatterlink
import matterlink.command.MatterLinkCommandSender
import matterlink.config.BaseConfig
import matterlink.config.cfg
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChatComponentText
import net.minecraftforge.common.ForgeVersion
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

        cfg = BaseConfig(event.modConfigurationDirectory).load()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        this.registerBridgeCommands()
    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        logger.debug("Registering server commands")
        event.registerServerCommand(CommandMatterlink())
        start()
    }

    @Mod.EventHandler
    fun serverStopping(event: FMLServerStoppingEvent) {
        stop()
    }

    //FORGE-DEPENDENT
    override fun wrappedSendToPlayers(msg: String) {
        MinecraftServer.getServer().configurationManager.sendChatMsg(ChatComponentText(msg))
    }

    override fun log(level: String, formatString: String, vararg data: Any) =
            logger.log(Level.toLevel(level, Level.INFO), formatString, *data)

    override fun commandSenderFor(user: String, userId: String, server: String, op: Boolean) = MatterLinkCommandSender(user, userId, server, op)

    override val mcVersion: String = MCVERSION
    override val modVersion: String = MODVERSION
    override val forgeVersion = ForgeVersion.getVersion()
}
