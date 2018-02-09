package matterlink

import matterlink.bridge.MessageHandler
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.bridge.command.HelpCommand
import matterlink.bridge.command.PlayerListCommand
import matterlink.command.CommandMatterlink
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.*

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
object MatterLink : IMatterLink() {
    init {
        instance = this
    }

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
    fun serverAboutToStart(event: FMLServerAboutToStartEvent) {
        MessageHandler.start()
    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        logger.debug("Registering server commands")
        event.registerServerCommand(CommandMatterlink())

        MessageHandler.rcvQueue.clear()
    }

    @Mod.EventHandler
    fun serverStopping(event: FMLServerStoppingEvent) {
        MessageHandler.stop()
    }

    //FORGE-DEPENDENT
    override fun wrappedSendToPlayers(msg: String) {
        FMLCommonHandler.instance().minecraftServerInstance.playerList.sendMessage(TextComponentString(msg))
    }

    //FORGE-DEPENDENT
    override fun wrappedPlayerList(): Array<String> {
        return FMLCommonHandler.instance().minecraftServerInstance.playerList.onlinePlayerNames
    }
    
}
