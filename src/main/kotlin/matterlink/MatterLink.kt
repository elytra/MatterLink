package matterlink

import matterlink.bridge.MessageHandler
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.bridge.command.HelpCommand
import matterlink.bridge.command.PlayerListCommand
import matterlink.command.CommandMatterlink
import matterlink.handlers.*
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLCommonHandler
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
    }

    @Mod.EventHandler
    fun serverStopping(event: FMLServerStoppingEvent) {
        MessageHandler.stop()
    }

    //FORGE-DEPENDENT
    fun wrappedSendToPlayers(msg: String) {
        FMLCommonHandler.instance().minecraftServerInstance.playerList.sendMessage(TextComponentString(msg))
    }

    //FORGE-DEPENDENT
    fun wrappedPlayerList(): Array<String> {
        return FMLCommonHandler.instance().minecraftServerInstance.playerList.onlinePlayerNames
    }
    
}
