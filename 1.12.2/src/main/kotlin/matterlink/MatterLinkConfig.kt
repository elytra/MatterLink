package matterlink

import net.minecraftforge.common.config.Configuration
import java.io.File
import java.util.regex.Pattern

class MatterLinkConfig(file: File) : IMatterLinkConfig() {
    init {
        logger.info("Reading bridge blueprints... from {}", file)
        val config = Configuration(file)

        config.addCustomCategoryComment(CATEGORY_RELAY_OPTIONS, "Relay options")
        relay = RelayOptions(

                systemUser = config.getString(
                        "systemUser",
                        CATEGORY_RELAY_OPTIONS,
                        relay.systemUser,
                        "Name of the server user (used by death and advancement messages and the /say command)"
                ),
                deathEvents = config.getBoolean(
                        "deathEvents",
                        CATEGORY_RELAY_OPTIONS,
                        relay.deathEvents,
                        "Relay player death messages"
                ),
                advancements = config.getBoolean(
                        "advancements",
                        CATEGORY_RELAY_OPTIONS,
                        relay.advancements,
                        "Relay player advancements"
                ),
                joinLeave = config.getBoolean(
                        "joinLeave",
                        CATEGORY_RELAY_OPTIONS,
                        relay.joinLeave,
                        "Relay when a player joins or leaves the game"
                )
        )

        config.addCustomCategoryComment(CATEGORY_COMMAND, "User commands")
        command = CommandOptions(
                enable = config.getBoolean(
                        "enable",
                        CATEGORY_COMMAND,
                        command.enable,
                        "Enable MC bridge commands"
                ),
                prefix = config.getString(
                        "prefix",
                        CATEGORY_COMMAND,
                        command.prefix,
                        "Prefix for MC bridge commands. Accepts a single character (not alphanumeric or /)",
                        Pattern.compile("^[^0-9A-Za-z/]$")
                )
        )

        config.addCustomCategoryComment(CATEGORY_FORMATTING, "Formatting options: " +
                "Available variables: {username}, {text}, {gateway}, {channel}, {protocol}, {username:antiping}")
        formatting = FormattingOptions(
                chat = config.getString(
                        "chat",
                        CATEGORY_FORMATTING,
                        formatting.chat,
                        "Generic chat event, just talking"
                ),
                joinLeave = config.getString(
                        "joinLeave",
                        CATEGORY_FORMATTING,
                        formatting.joinLeave,
                        "Join and leave events from other gateways"
                ),
                action = config.getString(
                        "action",
                        CATEGORY_FORMATTING,
                        formatting.action,
                        "User actions (/me) sent by users from other gateways"
                )
        )

        config.addCustomCategoryComment(CATEGORY_CONNECTION, "Connection settings")
        connect = ConnectOptions(
                url = config.getString(
                        "connectURL",
                        CATEGORY_CONNECTION,
                        connect.url,
                        "The URL or IP address of the bridge server"
                ),
                authToken = config.getString(
                        "authToken",
                        CATEGORY_CONNECTION,
                        connect.authToken,
                        "Auth token used to connect to the bridge server"
                ),
                gateway = config.getString(
                        "gateway",
                        CATEGORY_CONNECTION,
                        connect.gateway,
                        "MatterBridge gateway"
                )
        )

        if (config.hasChanged()) config.save()

        cfg = this
    }
}