package matterlink

import com.mojang.authlib.GameProfile
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.event.FMLServerStartingEvent
import cpw.mods.fml.common.event.FMLServerStoppingEvent
import matterlink.bridge.command.IBridgeCommand
import matterlink.command.MatterLinkCommand
import matterlink.command.MatterLinkCommandSender
import matterlink.config.BaseConfig
import matterlink.config.cfg
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChatComponentText
import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.common.MinecraftForge
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import java.util.*

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
        event.registerServerCommand(MatterLinkCommand())
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

    override fun wrappedSendToPlayer(user: String, msg: String) {
        val profile = profileByName(user) ?: profileByUUID(user) ?: run {
            error("cannot find player by name or uuid $user")
            return
        }
        val player = playerByProfile(profile) ?: run {
            error("${profile.name} is not online")
            return
        }
        player.addChatMessage(ChatComponentText(msg))
    }

    override fun isOnline(username: String) = (FMLCommonHandler.instance()
            .minecraftServerInstance.configurationManager.getPlayerByUsername(username) ?: null) != null

    private fun playerByProfile(gameProfile: GameProfile): EntityPlayerMP? {
        return FMLCommonHandler.instance().minecraftServerInstance.configurationManager.createPlayerForUser(gameProfile)
    }

    private fun profileByUUID(uuid: String): GameProfile? = try {
        FMLCommonHandler.instance().minecraftServerInstance.playerProfileCache.func_152652_a(UUID.fromString(uuid))
    } catch (e: IllegalArgumentException) {
        warn("cannot find profile by uuid $uuid")
        null
    }

    private fun profileByName(username: String): GameProfile? = try {
        FMLCommonHandler.instance().minecraftServerInstance.playerProfileCache.getGameProfileForUsername(username)
    } catch (e: IllegalArgumentException) {
        warn("cannot find profile by username $username")
        null
    }

    override fun nameToUUID(username: String) = profileByName(username)?.id?.toString()

    override fun uuidToName(uuid: String?): String? {
        return uuid?.let { profileByUUID(it)?.name }
    }

    override fun log(level: String, formatString: String, vararg data: Any) =
            logger.log(Level.toLevel(level, Level.INFO), formatString, *data)

    override fun commandSenderFor(
            user: String,
            env: IBridgeCommand.CommandEnvironment,
            op: Boolean
    ) = MatterLinkCommandSender(user, env, op)

    override val mcVersion: String = MCVERSION
    override val modVersion: String = MODVERSION
    override val forgeVersion = ForgeVersion.getVersion()
}
