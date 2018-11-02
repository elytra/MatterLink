package matterlink

import com.mojang.authlib.GameProfile
import kotlinx.coroutines.runBlocking
import matterlink.bridge.command.IBridgeCommand
import matterlink.command.AuthCommand
import matterlink.command.MatterLinkCommand
import matterlink.command.MatterLinkCommandSender
import matterlink.config.BaseConfig
import matterlink.config.cfg
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent
import java.util.UUID

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
        MinecraftForge.EVENT_BUS.register(EventHandler)
        logger = with(event.modLog) {
            object : Logger {
                override fun info(message: String) = this@with.info(message)
                override fun debug(message: String) = this@with.debug(message)
                override fun error(message: String) = this@with.error(message)
                override fun warn(message: String) = this@with.warn(message)
                override fun trace(message: String) = this@with.trace(message)
            }
        }

        logger.info("Building bridge!")

        cfg = BaseConfig(event.modConfigurationDirectory).load()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        this.registerBridgeCommands()
    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) = runBlocking {
        logger.debug("Registering server commands")
        event.registerServerCommand(MatterLinkCommand)
        event.registerServerCommand(AuthCommand)
        start()
    }

    @Mod.EventHandler
    fun serverStopping(event: FMLServerStoppingEvent) = runBlocking {
        stop()
    }

    //FORGE-DEPENDENT
    override fun wrappedSendToPlayers(msg: String) {
        FMLCommonHandler.instance().minecraftServerInstance.playerList.sendChatMsg(TextComponentString(msg))
    }

    override fun wrappedSendToPlayer(username: String, msg: String) {
        val profile = profileByName(username) ?: run {
            error("cannot find player by name $username")
            return
        }
        val player = playerByProfile(profile) ?: run {
            error("${profile.name} is not online")
            return
        }
        player.addChatMessage(TextComponentString(msg))
    }

    override fun wrappedSendToPlayer(uuid: UUID, msg: String) {
        val profile = profileByUUID(uuid) ?: run {
            logger.error("cannot find player by uuid $uuid")
            return
        }
        val player = playerByProfile(profile) ?: run {
            logger.error("${profile.name} is not online")
            return
        }
        player.addChatMessage(TextComponentString(msg))
    }

    override fun isOnline(username: String) =
        FMLCommonHandler.instance().minecraftServerInstance.playerList.allUsernames.contains(username)

    private fun playerByProfile(gameProfile: GameProfile): EntityPlayerMP? =
        FMLCommonHandler.instance().minecraftServerInstance.playerList.getPlayerByUUID(gameProfile.id)


    private fun profileByUUID(uuid: UUID): GameProfile? = try {
        FMLCommonHandler.instance().minecraftServerInstance.playerProfileCache.getProfileByUUID(uuid)
    } catch (e: IllegalArgumentException) {
        logger.warn("cannot find profile by uuid $uuid")
        null
    }

    private fun profileByName(username: String): GameProfile? = try {
        FMLCommonHandler.instance().minecraftServerInstance.playerProfileCache.getGameProfileForUsername(username)
    } catch (e: IllegalArgumentException) {
        logger.warn("cannot find profile by username $username")
        null
    }

    override fun collectPlayers(area: Area): Set<UUID> {
        val playerList = FMLCommonHandler.instance().minecraftServerInstance.playerList
        val players = playerList.allProfiles
            .map { playerList.getPlayerByUUID(it.id) }
            .filter {
                (area.allDimensions || area.dimensions.contains(it.dimension))
                        && area.testInBounds(it.posX.toInt(), it.posY.toInt(), it.posZ.toInt())
            }
        return players.map { it.uniqueID }.toSet()
    }

    override fun nameToUUID(username: String): UUID? = profileByName(username)?.id

    override fun uuidToName(uuid: UUID): String? = profileByUUID(uuid)?.name

    override fun commandSenderFor(
        user: String,
        env: IBridgeCommand.CommandEnvironment,
        op: Boolean
    ) = MatterLinkCommandSender(user, env, op)

    override val mcVersion: String = MCVERSION
    override val modVersion: String = MODVERSION
    override val buildNumber = BUILD_NUMBER
    override val forgeVersion = ForgeVersion.getVersion()
}
