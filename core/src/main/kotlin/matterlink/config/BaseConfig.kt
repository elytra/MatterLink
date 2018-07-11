package matterlink.config

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonObject
import blue.endless.jankson.impl.Marshaller
import blue.endless.jankson.impl.SyntaxError
import matterlink.*
import matterlink.bridge.MessageHandlerInst
import java.io.File
import java.io.FileNotFoundException

lateinit var cfg: BaseConfig.MatterLinkConfig
lateinit var baseCfg: BaseConfig

data class BaseConfig(val rootDir: File) {
    val cfgDirectory: File = rootDir.resolve("matterlink")
    val configFile: File = cfgDirectory.resolve("matterlink.hjson")

    init {
        logger.info("Reading bridge blueprints... from {}", rootDir)
        baseCfg = this
    }

    data class MatterLinkConfig(
            val connect: ConnectOptions = ConnectOptions(),
            val incoming: IncomingOptions = IncomingOptions(),
            val outgoing: OutgoingOptions = OutgoingOptions(),
            val command: CommandOptions = CommandOptions(),
            val update: UpdateOptions = UpdateOptions()
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
            val gateway: String = "minecraft",
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
                    "explosion.player" to arrayOf("\uD83D\uDCA3", "\uD83D\uDCA5") //💣 💥
            )
    )

    data class JoinPartOptions(
            val enable: Boolean = true,
            val joinServer: String = "{username:antiping} has connected to the platform",
            val partServer: String = "{username:antiping} has disconnected from the platform"
    )

    data class UpdateOptions(
            val enable: Boolean = true
    )

    companion object {
        val jankson = Jankson
                .builder()
                .registerTypeAdapter {
                    MatterLinkConfig(
                            command = it.getOrDefault(
                                    "command",
                                    CommandOptions(),
                                    "User commands"
                            ),
                            connect = it.getOrDefault(
                                    "connect",
                                    ConnectOptions(),
                                    "Connection Settings"
                            ),
                            incoming = it.getOrDefault(
                                    "incoming",
                                    IncomingOptions(),
                                    """
     Gateway -> Server
     Options all about receiving messages from the API
     Formatting options:
     Available variables: {username}, {text}, {gateway}, {channel}, {protocol}, {username:antiping}
     """.trimIndent()
                            ),
                            outgoing = it.getOrDefault(
                                    "outgoing",
                                    OutgoingOptions(),
                                    """
     Server -> Gateway
     Options all about sending messages to the API
     """.trimIndent()
                            ),
                            update = it.getOrDefault(
                                    "update",
                                    UpdateOptions(),
                                    "Update Settings"
                            )
                    )
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
                                gateway = it.getOrDefault(
                                        "gateway",
                                        gateway,
                                        "MatterBridge gateway"
                                ),
                                autoConnect = it.getOrDefault(
                                        "autoConnect",
                                        autoConnect,
                                        "Connect the relay on startup"
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
                                            it.mapValues { (key, element) ->
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
                .build()
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
            MatterLinkConfig()
        } catch (e: IllegalStateException) {
            logger.error(e.stackTraceString)
            cfgDirectory.resolve("error.matterlink.hjson").writeText(jsonObject.toJson(false, true))
            MatterLinkConfig()
        } catch (e: NullPointerException) {
            logger.error("error loading config: ${e.stackTraceString}")
            cfgDirectory.resolve("error.matterlink.hjson").writeText(jsonObject.toJson(false, true))
            MatterLinkConfig()
        }

//        val defaultJsonObject = jankson.load("{}")
//        jankson.fromJson(defaultJsonObject, MatterLinkConfig::class.java)
//        val nonDefault = jsonObject.getDelta(defaultJsonObject)

        MessageHandlerInst.config.url = tmpCfg.connect.url
        MessageHandlerInst.config.token = tmpCfg.connect.authToken
        MessageHandlerInst.config.gateway = tmpCfg.connect.gateway
        MessageHandlerInst.config.reconnectWait = tmpCfg.connect.reconnectWait

        MessageHandlerInst.config.systemUser = tmpCfg.outgoing.systemUser
        MessageHandlerInst.config.announceConnect = tmpCfg.outgoing.announceConnect
        MessageHandlerInst.config.announceDisconnect = tmpCfg.outgoing.announceDisconnect

        return tmpCfg
    }
}