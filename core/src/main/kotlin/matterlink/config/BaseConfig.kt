package matterlink.config

import java.io.File
import java.util.regex.Pattern

var cfg: BaseConfig? = null

abstract class BaseConfig(val file : File) {
    companion object {
        private val CATEGORY_RELAY_OPTIONS = "relay"
        private val CATEGORY_FORMATTING_INCOMING = "formatting"
        private val CATEGORY_JOIN_LEAVE = "join_leave"
        private val CATEGORY_CONNECTION = "connection"
        private val CATEGORY_COMMAND = "command"
        private val CATEGORY_DEATH = "death"

        fun reload() {
            cfg = cfg!!.javaClass.getConstructor(cfg!!.file.javaClass).newInstance(cfg!!.file)
        }
    }

    var relay: RelayOptions = RelayOptions()
    var connect: ConnectOptions = ConnectOptions()
    var formatting: FormattingOptions = FormattingOptions()
    var joinLeave: FormattingJoinLeave = FormattingJoinLeave()
    var command: CommandOptions = CommandOptions()
    var death: DeathOptions = DeathOptions()

    data class RelayOptions(
            var systemUser: String = "Server",
            var advancements: Boolean = true,
            var logLevel: String = "INFO"
    )

    data class FormattingOptions(
            var chat: String = "<{username}> {text}",
            var joinLeave: String = "§6-- {username} {text}",
            var action: String = "§5* {username} {text}"
    )

    data class FormattingJoinLeave(
            var showJoin: Boolean = true,
            var showLeave: Boolean = true,
            var joinServer: String = "{username:antiping} has connected to the server",
            var leaveServer: String = "{username:antiping} has disconnected from the server"
    )

    data class ConnectOptions(
            var url: String = "http://localhost:4242",
            var authToken: String = "",
            var gateway: String = "minecraft"
    )

    data class CommandOptions(
            var prefix: String = "$",
            var enable: Boolean = true
    )

    data class DeathOptions(
            var showDeath: Boolean = true,
            var showDamageType: Boolean = true,
            var damageTypeMapping: Map<String, String> = mapOf(
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
                    "explosion.player" to "\uD83D\uDCA3 \uD83D\uDCA5", //💣 💥

                    "electrocut" to "⚡",
                    "radiation" to "☢"
            )
    )

    protected fun load(
            getBoolean: (key: String, category: String, default: Boolean, comment: String) -> Boolean,
            getString: (key: String, category: String, default: String, comment: String) -> String,
            getStringValidated: (key: String, category: String, default: String, comment: String, pattern: Pattern) -> String,
            getStringValidValues: (key: String, category: String, default: String, comment: String, validValues: Array<String>) -> String,
            addCustomCategoryComment: (key: String, comment: String) -> Unit,
            getStringList: (name: String, category: String, defaultValues: Array<String>, comment: String) -> Array<String>
    ) {
        var category = CATEGORY_RELAY_OPTIONS
        addCustomCategoryComment(CATEGORY_RELAY_OPTIONS, "Relay options")
        relay = RelayOptions(
                systemUser = getString(
                        "systemUser",
                        category,
                        relay.systemUser,
                        "Name of the server user (used by death and advancement messages and the /say command)"
                ),
                advancements = getBoolean(
                        "advancements",
                        category,
                        relay.advancements,
                        "Relay player advancements"
                ),
                logLevel = getStringValidValues(
                        "logLevel",
                        category,
                        relay.logLevel,
                        "MatterLink log level",
                        arrayOf("INFO", "DEBUG", "TRACE")
                )
        )

        category = CATEGORY_COMMAND
        addCustomCategoryComment(CATEGORY_COMMAND, "User commands")
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

        category = CATEGORY_FORMATTING_INCOMING
        addCustomCategoryComment(CATEGORY_FORMATTING_INCOMING, "Gateway -> Server" +
                "Formatting options: " +
                "Available variables: {username}, {text}, {gateway}, {channel}, {protocol}, {username:antiping}")
        formatting = FormattingOptions(
                chat = getString(
                        "chat",
                        category,
                        formatting.chat,
                        "Generic chat event, just talking"
                ),
                joinLeave = getString(
                        "joinLeave",
                        category,
                        formatting.joinLeave,
                        "Join and leave events from other gateways"
                ),
                action = getString(
                        "action",
                        category,
                        formatting.action,
                        "User actions (/me) sent by users from other gateways"
                )
        )

        category = CATEGORY_JOIN_LEAVE
        addCustomCategoryComment(CATEGORY_JOIN_LEAVE, "Server -> Gateway" +
                "Formatting options: " +
                "Available variables: {username}, {username:antiping}")
        joinLeave = FormattingJoinLeave(

                showJoin = getBoolean(
                        "showJoin",
                        category,
                        joinLeave.showJoin,
                        "Relay when a player joins the game"
                ),

                showLeave = getBoolean(
                        "showLeave",
                        category,
                        joinLeave.showLeave,
                        "Relay when a player leaves the game"
                ),
                joinServer = getString(
                        "joinServer",
                        category,
                        joinLeave.joinServer,
                        "user join message sent to other gateways, available variables: {username}, {username:antiping}"
                ),
                leaveServer = getString(
                        "leaveServer",
                        category,
                        joinLeave.leaveServer,
                        "user leave message sent to other gateways, available variables: {username}, {username:antiping}"
                )
        )

        category = CATEGORY_CONNECTION
        addCustomCategoryComment(CATEGORY_CONNECTION, "Connection settings")
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
                )
        )
        category = CATEGORY_DEATH
        addCustomCategoryComment(category, "Death message settings")
        death = DeathOptions(
                showDeath = getBoolean(
                        "showDeath",
                        category,
                        death.showDeath,
                        "Relay player death messages"
                ),
                showDamageType = getBoolean(
                        "showDamageType",
                        category,
                        death.showDamageType,
                        "Enable Damage type symbols on death messages"
                ),
                damageTypeMapping = getStringList(
                        "damageTypeMapping",
                        category,
                        death.damageTypeMapping.map { entry ->
                            "${entry.key}=${entry.value}"
                        }
                                .toTypedArray(),
                        "Damage type mapping for everything else, " +
                                "\nseperate value and key with '=', " +
                                "\nseperate multiple values with spaces\n"
                ).associate {
                    val key = it.substringBefore('=')
                    val value = it.substringAfter('=')
                    Pair(key, value)
                }
        )
    }
}