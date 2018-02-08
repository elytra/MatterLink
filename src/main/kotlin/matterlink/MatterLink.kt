package matterlink

import matterlink.bridge.MessageHandler
import matterlink.bridge.ServerChatHelper
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.bridge.command.HelpCommand
import matterlink.bridge.command.PlayerListCommand
import matterlink.command.CommandMatterlink
import matterlink.eventhandlers.*
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.*
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.message.SimpleMessageFactory
import org.apache.logging.log4j.simple.SimpleLogger
import org.apache.logging.log4j.util.PropertiesUtil
import java.util.*

const val MODID = "matterlink"
const val NAME = "MatterLink"
const val VERSION = "@VERSION@"

@Mod(
        modid = MODID,
        name = NAME, version = VERSION,
        serverSideOnly = true,
        useMetadata = true,
        acceptableRemoteVersions = "*",
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter"
)
object MatterLink {
    //create fake logger to get around Nullability
    var logger: Logger = SimpleLogger("",
            Level.OFF,
            false,
            false,
            false,
            false,
            "",
            SimpleMessageFactory(),
            PropertiesUtil(Properties()),
            System.out)

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
        logger.info("Building bridge!")

        MatterLinkConfig(event.suggestedConfigurationFile)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        BridgeCommandRegistry.registerAll(PlayerListCommand, HelpCommand)
    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        logger.debug("Registering server commands")
        event.registerServerCommand(CommandMatterlink())
        MessageHandler.start()

        //maybe try registering them manually
        MinecraftForge.EVENT_BUS.register(ServerChatHelper())
        MinecraftForge.EVENT_BUS.register(ChatMessageHandler())
        MinecraftForge.EVENT_BUS.register(DeathEventHandler())
        MinecraftForge.EVENT_BUS.register(CommandEventHandler())
        MinecraftForge.EVENT_BUS.register(AdvancementEventHandler())
        MinecraftForge.EVENT_BUS.register(JoinLeaveHandler())
    }

    @Mod.EventHandler
    fun serverStopping(event: FMLServerStoppingEvent) {
        MessageHandler.stop()
    }


}
