package matterlink

import jline.internal.Log
import matterlink.bridge.MessageHandler
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.bridge.command.HelpCommand
import matterlink.bridge.command.PlayerListCommand
import matterlink.command.CommandMatterlink
import matterlink.config.cfg
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.*
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger

const val MODID = "matterlink"
const val NAME = "MatterLink"
const val VERSION = "@VERSION@"

lateinit var logger: Logger

@Mod(
        modid = MODID,
        name = NAME, version = VERSION,
        serverSideOnly = true,
        useMetadata = true,
        acceptableRemoteVersions = "*",
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
        dependencies = "required-after:forgelin@[@FORGELIN-VERSION@,);required-after:forge@[@FORGE-VERSION@,);"
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
}
