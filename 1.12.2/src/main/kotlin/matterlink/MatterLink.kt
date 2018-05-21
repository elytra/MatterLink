package matterlink

import matterlink.command.CommandMatterlink
import matterlink.command.MatterLinkCommandSender
import matterlink.config.cfg
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.*
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger

lateinit var logger: Logger

@Mod(
        modid = MODID,
        name = NAME, version = MODVERSION,
        serverSideOnly = true,
        useMetadata = true,
        acceptableRemoteVersions = "*",
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
        dependencies = DEPENDENCIES
)
object MatterLink : IMatterLink() {
    init {
        instance = this
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
        logger.info("Building bridge!")

        cfg = MatterLinkConfig(event.modConfigurationDirectory)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        this.registerBridgeCommands()
    }

    @Mod.EventHandler
    fun serverAboutToStart(event: FMLServerAboutToStartEvent) {
//        MessageHandlerInst.start(clear = true)
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
        FMLCommonHandler.instance().minecraftServerInstance.playerList.sendMessage(TextComponentString(msg))
    }

    override fun log(level: String, formatString: String, vararg data: Any) =
            logger.log(Level.toLevel(level, Level.INFO), formatString, *data)

    override fun commandSenderFor(user: String, userId: String, server: String, op: Boolean) = MatterLinkCommandSender(user, userId, server, op)

    override val mcVersion: String = MCVERSION
    override val modVersion: String = MODVERSION
    override val forgeVersion = ForgeVersion.getVersion()
}
