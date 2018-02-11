package matterlink.config

import matterlink.instance
import java.util.regex.Pattern

var cfg: BaseConfig? = null



abstract class BaseConfig {
    companion object {
        private val CATEGORY_RELAY_OPTIONS = "relay"
        private val CATEGORY_FORMATTING_INCOMING = "formatting"
        private val CATEGORY_FORMATTING_JOIN_LEAVE = "formatting_join_leave"
        private val CATEGORY_CONNECTION = "connection"
        private val CATEGORY_COMMAND = "command"
    }

    var relay: RelayOptions = RelayOptions()
    var connect: ConnectOptions = ConnectOptions()
    var formatting: FormattingOptions = FormattingOptions()
    var formattingJoinLeave: FormattingJoinLeave = FormattingJoinLeave()
    var command: CommandOptions = CommandOptions()

    data class RelayOptions(
            var systemUser: String = "Server",
            var deathEvents: Boolean = true,
            var advancements: Boolean = true,
            var joinLeave: Boolean = true
    )

    data class FormattingOptions(
            var chat: String = "<{username}> {text}",
            var joinLeave: String = "§6-- {username} {text}",
            var action: String = "§5* {username} {text}"
    )

    data class FormattingJoinLeave(
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

    protected fun load(
            getBoolean: (key: String, category: String, default: Boolean, comment: String) -> Boolean,
            getString: (key: String, category: String, default: String, comment: String) -> String,
            getStringValidated: (key: String, category: String, default: String, comment: String, pattern: Pattern) -> String,
            addCustomCategoryComment: (key: String, comment: String) -> Unit
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
                deathEvents = getBoolean(
                        "deathEvents",
                        category,
                        relay.deathEvents,
                        "Relay player death messages"
                ),
                advancements = getBoolean(
                        "advancements",
                        category,
                        relay.advancements,
                        "Relay player advancements"
                ),
                joinLeave = getBoolean(
                        "joinLeave",
                        category,
                        relay.joinLeave,
                        "Relay when a player joins or leaves the game"
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

        category = CATEGORY_FORMATTING_JOIN_LEAVE
        addCustomCategoryComment(CATEGORY_FORMATTING_JOIN_LEAVE, "Server -> Gateway" +
                "Formatting options: " +
                "Available variables: {username}, {username:antiping}")
        formattingJoinLeave = FormattingJoinLeave(
                joinServer = getString(
                        "joinServer",
                        category,
                        formattingJoinLeave.joinServer,
                        "user join message sent to other gateways, available variables: {username}, {username:antiping}"
                ),
                leaveServer = getString(
                        "leaveServer",
                        category,
                        formattingJoinLeave.leaveServer,
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
    }
}