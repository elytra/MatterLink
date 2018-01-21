package civilengineering

import civilengineering.bridge.MessageHandler
import civilengineering.bridge.ServerChatHelper
import civilengineering.command.BridgeCommand
import civilengineering.eventhandlers.ChatMessageHandler
import civilengineering.eventhandlers.DeathEventHandler
import civilengineering.eventhandlers.UserActionHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.*
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.message.Message
import org.apache.logging.log4j.message.MessageFactory
import org.apache.logging.log4j.message.SimpleMessageFactory
import org.apache.logging.log4j.simple.SimpleLogger
import org.apache.logging.log4j.spi.AbstractLogger
import org.apache.logging.log4j.util.PropertiesUtil
import java.io.File
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

const val MODID = "civilengineering"
const val NAME = "Civil Engineering"
const val VERSION = "0.0.1"

@Mod(
        modid = MODID,
        name = NAME, version = VERSION,
        serverSideOnly = true,
        useMetadata = true,
        acceptableRemoteVersions = "*",
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter"
)
object CivilEngineering {
    init {
    }

    var config: Configuration = Configuration()

    //create fake logger to get around Nullability
    var logger: Logger = SimpleLogger ("",
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
        logger.info("loading logger")

        CivilEngineering.logger.info("Reading bridge blueprints...")
        val directory = event.modConfigurationDirectory
        config = Configuration(File(directory.path, "CivilEngineering.cfg"))
        Config.readConfig()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        logger.info("Bridge building init.")
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        if (config.hasChanged()) {
            config.save()
        }
        MinecraftForge.EVENT_BUS.register(ServerChatHelper::class.java)

    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        event.registerServerCommand(BridgeCommand())
        logger.info("Bridge building starting.")
        MessageHandler.start()

        //maybe try registering them manually
        MinecraftForge.EVENT_BUS.register(ChatMessageHandler())
        MinecraftForge.EVENT_BUS.register(DeathEventHandler())
        MinecraftForge.EVENT_BUS.register(UserActionHandler())
    }

    @Mod.EventHandler
    fun serverStopping(event: FMLServerStoppingEvent) {
        logger.info("Bridge shutting down.")
        MessageHandler.stop()
    }
}
