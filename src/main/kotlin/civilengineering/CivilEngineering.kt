package civilengineering

import civilengineering.bridge.MessageHandler
import civilengineering.bridge.ServerChatHelper
import civilengineering.command.BridgeCommand
import civilengineering.eventhandlers.ChatMessageHandler
import civilengineering.eventhandlers.DeathEventHandler
import civilengineering.eventhandlers.UserActionHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.*
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.message.SimpleMessageFactory
import org.apache.logging.log4j.simple.SimpleLogger
import org.apache.logging.log4j.util.PropertiesUtil
import java.util.*

const val MODID = "civilengineering"
const val NAME = "Civil Engineering"
const val VERSION = "0.1.1"

@Mod(
        modid = MODID,
        name = NAME, version = VERSION,
        serverSideOnly = true,
        useMetadata = true,
        acceptableRemoteVersions = "*",
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter"
)
object CivilEngineering {
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

        CivilEngineeringConfig(event.modConfigurationDirectory)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
//        MinecraftForge.EVENT_BUS.register(ServerChatHelper::class.java)

    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        logger.debug("Registering bridge commands")
        event.registerServerCommand(BridgeCommand())
        MessageHandler.start()

        //maybe try registering them manually
        MinecraftForge.EVENT_BUS.register(ServerChatHelper())
        MinecraftForge.EVENT_BUS.register(ChatMessageHandler())
        MinecraftForge.EVENT_BUS.register(DeathEventHandler())
        MinecraftForge.EVENT_BUS.register(UserActionHandler())
    }

    @Mod.EventHandler
    fun serverStopping(event: FMLServerStoppingEvent) {
        MessageHandler.stop()
    }
}
