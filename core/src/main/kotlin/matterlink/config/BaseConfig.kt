package matterlink.config

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonObject
import blue.endless.jankson.impl.Marshaller
import blue.endless.jankson.impl.SyntaxError
import matterlink.Area
import matterlink.bridge.MessageHandlerInst
import matterlink.getOrDefault
import matterlink.getOrPutList
import matterlink.getReifiedOrDelete
import matterlink.logger
import matterlink.registerSerializer
import matterlink.registerTypeAdapter
import matterlink.stackTraceString
import java.io.File
import java.io.FileNotFoundException

lateinit var cfg: BaseConfig.MatterLinkConfig
lateinit var baseCfg: BaseConfig

data class BaseConfig(val rootDir: File) {
    val cfgDirectory: File = rootDir.resolve("matterlink")
    val configFile: File = cfgDirectory.resolve("matterlink.hjson")

    init {
        logger.info("Reading bridge blueprints... from $rootDir")
        baseCfg = this
    }

    data class MatterLinkConfig(
        val connect: ConnectOptions = ConnectOptions(),
        val outgoingDefaults: DefaultSettingsOutgoing = DefaultSettingsOutgoing(),
        val incomingDefaults: DefaultSettingsIncoming = DefaultSettingsIncoming(),
        val locations: List<Location> = listOf(
            Location(
                label = "default",
                gateway = "minecraft",
                area = Area.Infinite(dimensions = listOf(-1, 0, 1), allDimensions = true),
                outgoing = SettingsOutgoing(
                    plain = true,
                    action = true,
                    join = true,
                    leave = true,
                    advancement = true,
                    death = true,
                    broadcast = true,
                    status = true
                ),
                incoming = SettingsIncoming(
                    plain = true,
                    action = true,
                    join_leave = true,
                    commands = true
                )
            )
        ),
        val incoming: IncomingOptions = IncomingOptions(),
        val outgoing: OutgoingOptions = OutgoingOptions(),
        val command: CommandOptions = CommandOptions(),
        val update: UpdateOptions = UpdateOptions()
    )

    data class DefaultSettingsOutgoing(
        val plain: Boolean = true,
        val action: Boolean = true,
        val join: Boolean = false,
        val leave: Boolean = false,
        val advancement: Boolean = false,
        val death: Boolean = false,
        val broadcast: Boolean = false,
        val status: Boolean = false
    )

    data class SettingsOutgoing(
        val plain: Boolean? = null,
        val action: Boolean? = null,
        val join: Boolean? = null,
        val leave: Boolean? = null,
        val advancement: Boolean? = null,
        val death: Boolean? = null,
        val broadcast: Boolean? = null,
        val status: Boolean? = null,
        val skip: List<String> = listOf()
    )

    data class DefaultSettingsIncoming(
        val plain: Boolean = true,
        val action: Boolean = true,
        val join_leave: Boolean = true,
        val commands: Boolean = true
    )

    data class SettingsIncoming(
        val plain: Boolean? = null,
        val action: Boolean? = null,
        val join_leave: Boolean? = null,
        val commands: Boolean? = null,
        val skip: List<String> = listOf()
    )

    data class Location(
        val label: String = "unlabeled",
        val gateway: String = "",
        val area: Area = Area.Infinite(),
        val outgoing: SettingsOutgoing = SettingsOutgoing(),
        val incoming: SettingsIncoming = SettingsIncoming()
    )

    data class CommandOptions(
        val prefix: Char = '!',
        val enable: Boolean = true,
        val authRequests: Boolean = true,
        val permisionRequests: Boolean = true,
        val defaultPermUnauthenticated: Double = 0.0,
        val defaultPermAuthenticated: Double = 1.0
    )

    data class ConnectOptions(
        val url: String = "http://localhost:4242",
        val authToken: String = "",
        val autoConnect: Boolean = true,
        val reconnectWait: Long = 500
    )

    data class IncomingOptions(
        val chat: String = "<{username}> {text}",
        val joinPart: String = "§6-- {username} {text}",
        val action: String = "§5* {username} {text}",
        val stripColors: Boolean = true
    )

    data class OutgoingOptions(
        val systemUser: String = "Server",
        //outgoing toggles
        val announceConnect: Boolean = true,
        val announceDisconnect: Boolean = true,
        val advancements: Boolean = true,
        val stripColors: Boolean = true,
        val pasteEEKey: String = "",
        val inlineLimit: Int = 5,

        val joinPart: JoinPartOptions = JoinPartOptions(),
        var avatar: AvatarOptions = AvatarOptions(),
        val death: DeathOptions = DeathOptions()
    )

    data class DeathOptions(
        val enable: Boolean = true,
        val damageType: Boolean = true,
        val damageTypeMapping: Map<String, Array<String>> = mapOf(
            "inFire" to arrayOf("\uD83D\uDD25"), //🔥
            "lightningBolt" to arrayOf("\uD83C\uDF29"), //🌩
            "onFire" to arrayOf("\uD83D\uDD25"), //🔥
            "lava" to arrayOf("\uD83D\uDD25"), //🔥
            "hotFloor" to arrayOf("♨️"),
            "inWall" to arrayOf(),
            "cramming" to arrayOf(),
            "drown" to arrayOf("\uD83C\uDF0A"), //🌊
            "starve" to arrayOf("\uD83D\uDC80"), //💀
            "cactus" to arrayOf("\uD83C\uDF35"), //🌵
            "fall" to arrayOf("\u2BEF️"), //⯯️
            "flyIntoWall" to arrayOf("\uD83D\uDCA8"), //💨
            "outOfWorld" to arrayOf("\u2734"), //✴
            "generic" to arrayOf("\uD83D\uDC7B"), //👻
            "magic" to arrayOf("✨", "⚚"),
            "indirectMagic" to arrayOf("✨", "⚚"),
            "wither" to arrayOf("\uD83D\uDD71"), //🕱
            "anvil" to arrayOf(),
            "fallingBlock" to arrayOf(),
            "dragonBreath" to arrayOf("\uD83D\uDC32"), //🐲
            "fireworks" to arrayOf("\uD83C\uDF86"), //🎆

            "mob" to arrayOf("\uD83D\uDC80"), //💀
            "player" to arrayOf("\uD83D\uDDE1"), //🗡
            "arrow" to arrayOf("\uD83C\uDFF9"), //🏹
            "thrown" to arrayOf("彡°"),
            "thorns" to arrayOf("\uD83C\uDF39"), //🌹
            "explosion" to arrayOf("\uD83D\uDCA3", "\uD83D\uDCA5"), //💣 💥
            "explosion.player" to arrayOf("\uD83D\uDCA3", "\uD83D\uDCA5"), //💣 💥
            "ieWireShock" to arrayOf("\uD83D\uDD0C", "\u26A1"), //🔌 ⚡
            "immersiverailroading:hitByTrain" to arrayOf(
                "\uD83D\uDE82",
                "\uD83D\uDE83",
                "\uD83D\uDE84",
                "\uD83D\uDE85",
                "\uD83D\uDE87",
                "\uD83D\uDE88",
                "\uD83D\uDE8A"
            ) //🚂 🚃 🚄 🚅 🚇 🚈 🚊
        )
    )

    data class AvatarOptions(
        val enable: Boolean = true,
        val urlTemplate: String = "https://visage.surgeplay.com/head/512/{uuid}",
        // https://www.freepik.com/free-icon/right-arrow-angle-and-horizontal-down-line-code-signs_732795.htm
        val systemUserAvatar: String = "https://image.freepik.com/free-icon/right-arrow-angle-and-horizontal-down-line-code-signs_318-53994.jpg"
    )

    data class JoinPartOptions(
        val enable: Boolean = true,
        val joinServer: String = "{username:antiping} has connected to the server",
        val partServer: String = "{username:antiping} has disconnected from the server"
    )

    data class UpdateOptions(
        val enable: Boolean = true
    )

    companion object {
        val jankson = Jankson
            .builder()
            .registerTypeAdapter {
                with(MatterLinkConfig()) {
                    MatterLinkConfig(
                        command = it.getOrDefault(
                            "command",
                            command,
                            "User commands"
                        ),
                        outgoingDefaults = it.getOrDefault(
                            "outgoingDefaults",
                            outgoingDefaults,
                            "default settings for outgoing"
                        ),
                        incomingDefaults = it.getOrDefault(
                            "incomingDefaults",
                            incomingDefaults,
                            "default settings for incoming"
                        ),
                        locations = it.getOrPutList(
                            "locations",
                            locations,
                            "list of fixed chat locations"
                        ),
                        connect = it.getOrDefault(
                            "connect",
                            connect,
                            "Connection Settings"
                        ),
                        incoming = it.getOrDefault(
                            "incoming",
                            incoming,
                            """
                                         Gateway -> Server
                                         Options all about receiving messages from the API
                                         Formatting options:
                                         Available variables: {username}, {text}, {gateway}, {channel}, {protocol}, {username:antiping}
                                         """.trimIndent()
                        ),
                        outgoing = it.getOrDefault(
                            "outgoing",
                            outgoing,
                            """
                                         Server -> Gateway
                                         Options all about sending messages to the API
                                         """.trimIndent()
                        ),
                        update = it.getOrDefault(
                            "update",
                            update,
                            "Update Settings"
                        )
                    )
                }
            }
            .registerTypeAdapter {
                with(DefaultSettingsOutgoing()) {
                    DefaultSettingsOutgoing(
                        plain = it.getOrDefault(
                            "plain",
                            plain,
                            "plain text messages"
                        ),
                        action = it.getOrDefault(
                            "action",
                            action,
                            "action messages"
                        ),
                        join = it.getOrDefault(
                            "join",
                            join,
                            "handle join event"
                        ),
                        leave = it.getOrDefault(
                            "leave",
                            leave,
                            "handle leave events"
                        ),
                        advancement = it.getOrDefault(
                            "advancement",
                            advancement,
                            "handle advancement events"
                        ),
                        death = it.getOrDefault(
                            "death",
                            death,
                            "handle death events"
                        ),
                        broadcast = it.getOrDefault(
                            "broadcast",
                            broadcast,
                            "handle broadcast command"
                        ),
                        status = it.getOrDefault(
                            "status",
                            status,
                            "handles tatus updates"
                        )
                    )
                }
            }
            .registerTypeAdapter {
                with(SettingsOutgoing()) {
                    SettingsOutgoing(
                        plain = it.getReifiedOrDelete("plain", "transmit join events"),
                        action = it.getReifiedOrDelete("action", "transmit join events"),
                        join = it.getReifiedOrDelete("join", "transmit join events"),
                        leave = it.getReifiedOrDelete("leave", "transmit leave events"),
                        advancement = it.getReifiedOrDelete("advancement", "transmit advancements"),
                        death = it.getReifiedOrDelete("death", "transmit death messages"),
                        broadcast = it.getReifiedOrDelete("say", "transmit broadcasts"),
                        status = it.getReifiedOrDelete("status", "transmit status updates"),
                        skip = it.getOrPutList(
                            "skip",
                            skip,
                            "list of other locations to ignore after handling this"
                        )
                    )
                }
            }

            .registerTypeAdapter {
                with(DefaultSettingsIncoming()) {
                    DefaultSettingsIncoming(
                        plain = it.getOrDefault(
                            "plain",
                            plain,
                            "plain text messages"
                        ),
                        action = it.getOrDefault(
                            "action",
                            action,
                            "action messages"
                        ),
                        join_leave = it.getOrDefault(
                            "join_leave",
                            join_leave,
                            "handle join/leave event"
                        ),
                        commands = it.getOrDefault(
                            "commands",
                            join_leave,
                            "receive commands"
                        )
                    )
                }
            }
            .registerTypeAdapter {
                with(SettingsIncoming()) {
                    SettingsIncoming(
                        plain = it.getReifiedOrDelete("plain", "transmit join events"),
                        action = it.getReifiedOrDelete("action", "transmit join events"),
                        join_leave = it.getReifiedOrDelete("join_leave", "transmit join_leave events"),
                        commands = it.getReifiedOrDelete("commands", "receive commands"),
                        skip = it.getOrPutList(
                            "skip",
                            skip,
                            "list of other locations to ignore after handling this"
                        )
                    )
                }
            }
            .registerTypeAdapter {
                with(Location()) {
                    Location(
                        label = it.getOrDefault(
                            "label",
                            label,
                            "location label for identification"
                        ),
                        gateway = it.getOrDefault(
                            "gateway",
                            gateway,
                            "matterbridge gateway identifier"
                        ),
                        area = Area.parse(it.getObject("area") ?: JsonObject()),
                        outgoing = it.getOrDefault(
                            "outgoing",
                            outgoing,
                            "Location outgoing settings"
                        ),
                        incoming = it.getOrDefault(
                            "incoming",
                            incoming,
                            "incoming settings"
                        )
                    )
                }
            }
            .registerTypeAdapter {
                with(CommandOptions()) {
                    CommandOptions(
                        enable = it.getOrDefault(
                            "enable",
                            enable,
                            "Enable MC bridge commands"
                        ),
                        prefix = it.getOrDefault(
                            "prefix",
                            prefix,
                            "Prefix for MC bridge commands. Accepts a single character (not alphanumeric or /)"
                        ),
                        authRequests = it.getOrDefault(
                            "authRequests",
                            authRequests,
                            "Enable the 'auth' command for linking chat accounts to uuid / ingame account"
                        ),
                        permisionRequests = it.getOrDefault(
                            "permisionRequests",
                            authRequests,
                            "Enable the 'request' command for requestion permissions from chat"
                        ),
                        defaultPermUnauthenticated = it.getOrDefault(
                            "defaultPermUnauthenticated",
                            defaultPermUnauthenticated,
                            "default permission level for unauthenticated players"
                        ),
                        defaultPermAuthenticated = it.getOrDefault(
                            "defaultPermAuthenticated",
                            defaultPermAuthenticated,
                            "default permission level for players that hve linked their accounts"
                        )
                    )
                }
            }
            .registerTypeAdapter {
                with(ConnectOptions()) {
                    ConnectOptions(
                        url = it.getOrDefault(
                            "url",
                            url,
                            "The URL or IP address of the bridge platform"
                        ),
                        authToken = it.getOrDefault(
                            "authToken",
                            authToken,
                            "Auth token used to connect to the bridge platform"
                        ),
                        autoConnect = it.getOrDefault(
                            "autoConnect",
                            autoConnect,
                            "Connect the relay on startup"
                        ),
                        reconnectWait = it.getOrDefault(
                            "reconnectWait",
                            reconnectWait,
                            "base delay in milliseconds between attempting reconnects"
                        )
                    )
                }
            }
            .registerTypeAdapter {
                with(IncomingOptions()) {
                    IncomingOptions(
                        chat = it.getOrDefault(
                            "chat",
                            chat,
                            "Generic chat event, just talking"
                        ),
                        joinPart = it.getOrDefault(
                            "joinPart",
                            joinPart,
                            "Join and part events from other gateways"
                        ),
                        action = it.getOrDefault(
                            "action",
                            action,
                            "User actions (/me) sent by users from other gateways"
                        ),
                        stripColors = it.getOrDefault(
                            "stripColors",
                            stripColors,
                            "strip colors from incoming text"
                        )
                    )
                }
            }
            .registerTypeAdapter {
                with(OutgoingOptions()) {
                    OutgoingOptions(
                        systemUser = it.getOrDefault(
                            "systemUser",
                            systemUser,
                            "Name of the platform user (used by death and advancement messages and the /say command)"
                        ),
                        advancements = it.getOrDefault(
                            "advancements",
                            advancements,
                            "Relay player achievements / advancements"
                        ),
                        announceConnect = it.getOrDefault(
                            "announceConnect",
                            announceConnect,
                            "announce successful connection to the gateway"
                        ),
                        announceDisconnect = it.getOrDefault(
                            "announceDisconnect",
                            announceConnect,
                            "announce intention to disconnect / reconnect"
                        ),
                        stripColors = it.getOrDefault(
                            "stripColors",
                            stripColors,
                            "strip colors from nicknames and messages"
                        ),
                        pasteEEKey = it.getOrDefault(
                            "pasteEEKey",
                            pasteEEKey,
                            "paste.ee api key, leave empty to use application default"
                        ),
                        inlineLimit = it.getOrDefault(
                            "inlineLimit",
                            inlineLimit,
                            "messages with more lines than this will get shortened via paste.ee"
                        ),
                        death = it.getOrDefault(
                            "death",
                            DeathOptions(),
                            "Death messages settings"
                        ),
                        avatar = it.getOrDefault(
                            "avatar",
                            AvatarOptions(),
                            "Avatar options"
                        ),
                        joinPart = it.getOrDefault(
                            "joinPart",
                            JoinPartOptions(),
                            "relay join and part messages to the gatway"
                        )
                    )
                }
            }
            .registerTypeAdapter { jsonObj ->
                with(DeathOptions()) {
                    DeathOptions(
                        enable = jsonObj.getOrDefault(
                            "enable",
                            enable,
                            "Relay player death messages"
                        ),
                        damageType = jsonObj.getOrDefault(
                            "damageType",
                            damageType,
                            "Enable Damage type symbols on death messages"
                        ),
                        damageTypeMapping = (jsonObj.getObject("damageTypeMapping")
                            ?: Marshaller.getFallback().serialize(damageTypeMapping) as JsonObject)
                            .let {
                                jsonObj.setComment(
                                    "damageTypMapping",
                                    "Damage type mapping for death cause"
                                )
                                it.mapValues { (key, _) ->
                                    it.getOrDefault(key, damageTypeMapping[key] ?: emptyArray(), key)
                                        .apply { it[key] }.apply {
                                            jsonObj["damageTypeMapping"] = it
                                        }
                                }
                            }
                    )
                }
            }
            .registerTypeAdapter {
                with(AvatarOptions()) {
                    AvatarOptions(
                        enable = it.getOrDefault(
                            "enable",
                            enable,
                            "enable ingame avatar"
                        ),
                        urlTemplate = it.getOrDefault(
                            "urlTemplate",
                            urlTemplate,
                            "template for constructing the user avatar url using the uuid"
                        )
                    )
                }
            }
            .registerTypeAdapter {
                with(JoinPartOptions()) {
                    JoinPartOptions(
                        enable = it.getOrDefault(
                            "enable",
                            enable,
                            "Relay when a player joins / parts the game" +
                                    "\nany receiving end still needs to be configured with showJoinPart = true" +
                                    "\nto display the messages"
                        ),
                        joinServer = it.getOrDefault(
                            "joinServer",
                            joinServer,
                            "user join message sent to other gateways, available variables: {username}, {username:antiping}"
                        ),
                        partServer = it.getOrDefault(
                            "partServer",
                            partServer,
                            "user part message sent to other gateways, available variables: {username}, {username:antiping}"
                        )
                    )
                }
            }
            .registerTypeAdapter {
                with(UpdateOptions()) {
                    UpdateOptions(
                        enable = it.getOrDefault(
                            "enable",
                            enable,
                            "Enable Update checking"
                        )
                    )
                }
            }
            .registerSerializer { locationSettings: SettingsOutgoing, marshaller: Marshaller ->
                val jsonObject = JsonObject()
                locationSettings.advancement?.let {
                    jsonObject["advancements"] = marshaller.serialize(it)
                }
                locationSettings.death?.let {
                    jsonObject["death"] = marshaller.serialize(it)
                }
                locationSettings.join?.let {
                    jsonObject["joins"] = marshaller.serialize(it)
                }
                locationSettings.leave?.let {
                    jsonObject["leaves"] = marshaller.serialize(it)
                }
                locationSettings.broadcast?.let {
                    jsonObject["say"] = marshaller.serialize(it)
                }
                jsonObject
            }
            .build()!!
    }

    fun load(): MatterLinkConfig {
        val jsonObject = try {
            jankson.load(configFile)
        } catch (e: SyntaxError) {
            logger.error("error loading config: ${e.completeMessage}")
            jankson.marshaller.serialize(MatterLinkConfig()) as JsonObject
        } catch (e: FileNotFoundException) {
            logger.error("creating config file $configFile")
            configFile.absoluteFile.parentFile.mkdirs()
            configFile.createNewFile()
            jankson.marshaller.serialize(MatterLinkConfig()) as JsonObject
        }
        logger.info("finished loading base config")

        val tmpCfg = try {
            //cfgDirectory.resolve("debug.matterlink.hjson").writeText(jsonObject.toJson(false, true))
            jankson.fromJson(jsonObject, MatterLinkConfig::class.java).apply {
                configFile.writeText(jsonObject.toJson(true, true))
                logger.info("loaded config: Main config")
                logger.debug("loaded config: $this")
            }
        } catch (e: SyntaxError) {
            logger.error("error parsing config: ${e.completeMessage} ")
            logger.error(e.stackTraceString)
            cfgDirectory.resolve("error.matterlink.hjson").writeText(jsonObject.toJson(false, true))
            if (::cfg.isInitialized) cfg else MatterLinkConfig()
        } catch (e: IllegalStateException) {
            logger.error(e.stackTraceString)
            cfgDirectory.resolve("error.matterlink.hjson").writeText(jsonObject.toJson(false, true))
            if (::cfg.isInitialized) cfg else MatterLinkConfig()
        } catch (e: NullPointerException) {
            logger.error("error loading config: ${e.stackTraceString}")
            cfgDirectory.resolve("error.matterlink.hjson").writeText(jsonObject.toJson(false, true))
            if (::cfg.isInitialized) cfg else MatterLinkConfig()
        }

//        val defaultJsonObject = jankson.load("{}")
//        jankson.fromJson(defaultJsonObject, MatterLinkConfig::class.java)
//        val nonDefault = jsonObject.getDelta(defaultJsonObject)

        MessageHandlerInst.config.url = tmpCfg.connect.url
        MessageHandlerInst.config.token = tmpCfg.connect.authToken
        MessageHandlerInst.config.reconnectWait = tmpCfg.connect.reconnectWait

        MessageHandlerInst.config.systemUser = tmpCfg.outgoing.systemUser
        MessageHandlerInst.config.announceConnect = tmpCfg.outgoing.announceConnect
        MessageHandlerInst.config.announceDisconnect = tmpCfg.outgoing.announceDisconnect

        return tmpCfg
    }
}