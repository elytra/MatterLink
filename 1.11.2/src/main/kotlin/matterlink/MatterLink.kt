package matterlink

import com.mojang.authlib.GameProfile
import matterlink.command.MatterLinkCommand
import matterlink.command.MatterLinkCommandSender
import matterlink.config.BaseConfig
import matterlink.config.cfg
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import java.util.*

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
        FMLCommonHandler.instance().minecraftServerInstance.playerList.sendMessage(TextComponentString(msg))
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
        player.sendMessage(TextComponentString(msg))
    }

    override fun isOnline(username: String) = FMLCommonHandler.instance().minecraftServerInstance.onlinePlayerNames.contains(username)

    private fun playerByProfile(gameProfile: GameProfile): EntityPlayerMP? = FMLCommonHandler.instance().minecraftServerInstance.playerList.getPlayerByUUID(gameProfile.id)


    private fun profileByUUID(uuid: String): GameProfile? = try {
        FMLCommonHandler.instance().minecraftServerInstance.playerProfileCache.getProfileByUUID(UUID.fromString(uuid))
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
            userId: String,
            server: String,
            uuid: String?,
            username: String?,
            op: Boolean
    ) = MatterLinkCommandSender(user, userId, server, uuid, username, op)

    override val mcVersion: String = MCVERSION
    override val modVersion: String = MODVERSION
    override val forgeVersion = ForgeVersion.getVersion()
}
