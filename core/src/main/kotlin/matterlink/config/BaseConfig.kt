package matterlink.config

import java.io.File
import java.util.regex.Pattern

lateinit var cfg: BaseConfig

abstract class BaseConfig(rootDir: File) {
    val cfgDirectory: File = rootDir.resolve("matterlink")
    val mainCfgFile: File = cfgDirectory.resolve("matterlink.cfg")


    var connect = ConnectOptions()
    var debug = DebugOptions()
    var incoming = IncomingOption()
    var outgoing = OutgoingOptions()
    var command = CommandOptions()
    var update = UpdateOptions()


    data class CommandOptions(
            val prefix: String = "!",
            val enable: Boolean = true
    )

    data class ConnectOptions(
            val url: String = "http://localhost:4242",
            val authToken: String = "",
            val gateway: String = "minecraft",
            val autoConnect: Boolean = true
    )

    data class DebugOptions(
            var logLevel: String = "INFO"
    )

    data class IncomingOption(
            val chat: String = "<{username}> {text}",
            val joinPart: String = "§6-- {username} {text}",
            val action: String = "§5* {username} {text}"
    )

    data class OutgoingOptions(
            val systemUser: String = "Server",
            //outgoing toggles
            var announceConnect: Boolean = true,
            var announceDisconnect: Boolean = true,
            val advancements: Boolean = true,
            var death: DeathOptions = DeathOptions(),

            var joinPart: JoinPartOptions = JoinPartOptions()
    )

    data class DeathOptions(
            val enable: Boolean = true,
            val damageType: Boolean = true,
            val damageTypeMapping: Map<String, String> = mapOf(
                    "inFire" to "\uD83D\uDD25", //🔥
                    "lightningBolt" to "\uD83C\uDF29", //🌩
                    "onFire" to "\uD83D\uDD25", //🔥
                    "lava" to "\uD83D\uDD25", //🔥
                    "hotFloor" to "♨️",
                    "inWall" to "",
                    "cramming" to "",
                    "drown" to "\uD83C\uDF0A", //🌊
                    "starve" to "\uD83D\uDC80", //💀
                    "cactus" to "\uD83C\uDF35", //🌵
                    "fall" to "\u2BEF️", //⯯️
                    "flyIntoWall" to "\uD83D\uDCA8", //💨
                    "outOfWorld" to "\u2734", //✴
                    "generic" to "\uD83D\uDC7B", //👻
                    "magic" to "✨ ⚚",
                    "indirectMagic" to "✨ ⚚",
                    "wither" to "\uD83D\uDD71", //🕱
                    "anvil" to "",
                    "fallingBlock" to "",
                    "dragonBreath" to "\uD83D\uDC32", //🐲
                    "fireworks" to "\uD83C\uDF86", //🎆

                    "mob" to "\uD83D\uDC80", //💀
                    "player" to "\uD83D\uDDE1", //🗡
                    "arrow" to "\uD83C\uDFF9", //🏹
                    "thrown" to "彡°",
                    "thorns" to "\uD83C\uDF39", //🌹
                    "explosion" to "\uD83D\uDCA3 \uD83D\uDCA5", //💣 💥
                    "explosion.player" to "\uD83D\uDCA3 \uD83D\uDCA5" //💣 💥
            )
    )

    data class JoinPartOptions(
            val enable: Boolean = true,
            val joinServer: String = "{username:antiping} has connected to the server",
            val partServer: String = "{username:antiping} has disconnected from the server"
    )

    data class UpdateOptions(
            val enable: Boolean = true
    )

    protected fun load(
            getBoolean: (key: String, category: String, default: Boolean, comment: String) -> Boolean,
            getString: (key: String, category: String, default: String, comment: String) -> String,
            getStringValidated: (key: String, category: String, default: String, comment: String, pattern: Pattern) -> String,
            getStringValidValues: (key: String, category: String, default: String, comment: String, validValues: Array<String>) -> String,
            getStringList: (name: String, category: String, defaultValues: Array<String>, comment: String) -> Array<String>,
            addCustomCategoryComment: (key: String, comment: String) -> Unit
    ) {

        var category = "commands"

        addCustomCategoryComment(category, "User commands")
        command = CommandOptions(
                enable = getBoolean(
                        "enable",
                        category,
                        command.enable,
                        "Enable MC bridge commands"
                ),
                prefix = getStringValidated(
                        "prefix",
                        category,
                        command.prefix,
                        "Prefix for MC bridge commands. Accepts a single character (not alphanumeric or /)",
                        Pattern.compile("^[^0-9A-Za-z/]$")
                )
        )

        category = "connection"
        addCustomCategoryComment(category, "Connection settings")
        connect = ConnectOptions(
                url = getString(
                        "connectURL",
                        category,
                        connect.url,
                        "The URL or IP address of the bridge server"
                ),
                authToken = getString(
                        "authToken",
                        category,
                        connect.authToken,
                        "Auth token used to connect to the bridge server"
                ),
                gateway = getString(
                        "gateway",
                        category,
                        connect.gateway,
                        "MatterBridge gateway"
                ),
                autoConnect = getBoolean(
                        "autoConnect",
                        category,
                        connect.autoConnect,
                        "Connect the relay on startup"
                )
        )

        category = "debug"
        addCustomCategoryComment(category, "Options to help you figure out what happens and why, because computers can be silly")
        debug = DebugOptions(
                logLevel = getStringValidValues(
                        "logLevel",
                        category,
                        debug.logLevel,
                        "MatterLink log level",
                        arrayOf("INFO", "DEBUG", "TRACE")
                )
        )

        category = "incoming"
        addCustomCategoryComment(category, "Gateway -> Server" +
                "\nOptions all about receiving messages from the API" +
                "\nFormatting options: " +
                "\nAvailable variables: {username}, {text}, {gateway}, {channel}, {protocol}, {username:antiping}")
        incoming = IncomingOption(
                chat = getString(
                        "chat",
                        category,
                        incoming.chat,
                        "Generic chat event, just talking"
                ),
                joinPart = getString(
                        "joinPart",
                        category,
                        incoming.joinPart,
                        "Join and part events from other gateways"
                ),
                action = getString(
                        "action",
                        category,
                        incoming.action,
                        "User actions (/me) sent by users from other gateways"
                )
        )

        category = "outgoing"
        addCustomCategoryComment(category, "Server -> Gateway" +
                "\nOptions all about sending messages to the API")

        outgoing = OutgoingOptions(
                systemUser = getString(
                        "systemUser",
                        category,
                        outgoing.systemUser,
                        "Name of the server user (used by death and advancement messages and the /say command)"
                ),
                //outgoing events toggle
                advancements = getBoolean(
                        "advancements",
                        category,
                        outgoing.advancements,
                        "Relay player achievements / advancements"
                ),
                announceConnect = getBoolean(
                        "announceConnect",
                        category,
                        outgoing.announceConnect,
                        "announce successful connection to the gateway"
                ),
                announceDisconnect = getBoolean(
                        "announceDisconnect",
                        category,
                        outgoing.announceConnect,
                        "announce intention to disconnect / reconnect"
                )
        )

        category = "outgoing.death"
        addCustomCategoryComment(category, "Death messages settings")
        outgoing.death = DeathOptions(

                enable = getBoolean(
                        "enable",
                        category,
                        outgoing.death.enable,
                        "Relay player death messages"
                ),
                damageType = getBoolean(
                        "damageType",
                        category,
                        outgoing.death.damageType,
                        "Enable Damage type symbols on death messages"
                ),
                damageTypeMapping = getStringList(
                        "damageTypeMapping",
                        category,
                        outgoing.death.damageTypeMapping.map { entry ->
                            "${entry.key}=${entry.value}"
                        }
                                .toTypedArray(),
                        "Damage type mapping for death cause, " +
                                "\nseparate value and key with '=', " +
                                "\nseparate multiple values with spaces\n"
                ).associate {
                    val key = it.substringBefore('=')
                    val value = it.substringAfter('=')
                    Pair(key, value)
                }
        )

        category = "outgoing.join&part"
        addCustomCategoryComment(category, "relay join and part messages to the gatway" +
                "\nFormatting options: " +
                "\nAvailable variables: {username}, {username:antiping}")
        outgoing.joinPart = JoinPartOptions(
                enable = getBoolean(
                        "enable",
                        category,
                        outgoing.joinPart.enable,
                        "Relay when a player joins / parts the game" +
                                "\nany receiving end still needs to be configured with showJoinPart = true" +
                                "\nto display the messages"
                ),
                joinServer = getString(
                        "joinServer",
                        category,
                        outgoing.joinPart.joinServer,
                        "user join message sent to other gateways, available variables: {username}, {username:antiping}"
                ),
                partServer = getString(
                        "partServer",
                        category,
                        outgoing.joinPart.partServer,
                        "user part message sent to other gateways, available variables: {username}, {username:antiping}"
                )
        )



        category = "update"
        addCustomCategoryComment(category, "Update Settings")
        update = UpdateOptions(
                enable = getBoolean(
                        "enable",
                        category,
                        update.enable,
                        "Enable Update checking"
                )
        )
    }

    abstract fun load(): BaseConfig
}