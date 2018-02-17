package matterlink

import matterlink.command.CommandMatterlink
import matterlink.command.IMinecraftCommandSender
import matterlink.command.MatterlinkCommandSender
import matterlink.config.cfg
import matterlink.update.UpdateChecker
import net.minecraft.util.text.TextComponentString
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

        cfg = MatterLinkConfig(event.suggestedConfigurationFile)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
       this.registerBridgeCommands()
    }

    @Mod.EventHandler
    fun serverAboutToStart(event: FMLServerAboutToStartEvent) {
//        MessageHandler.start(clear = true)
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
        FMLCommonHandler.instance().minecraftServerInstance.playerList.sendMessage(TextComponentString(msg))
    }

    //FORGE-DEPENDENT
    override fun wrappedPlayerList(): Array<String> {
        return FMLCommonHandler.instance().minecraftServerInstance.playerList.onlinePlayerNames
    }

    override fun log(level: String, formatString: String, vararg data: Any) =
            logger.log(Level.toLevel(level, Level.INFO),formatString, *data)

    override var commandSender: IMinecraftCommandSender = MatterlinkCommandSender

    override val mcVersion: String = MCVERSION
    override val modVersion: String = MODVERSION
}
