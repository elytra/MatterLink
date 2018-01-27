package matterlink

import net.minecraftforge.common.config.Configuration
import java.io.File

var cfg: MatterLinkConfig? = null

class MatterLinkConfig() {
    private val CATEGORY_RELAY_OPTIONS = "relay"
    private val CATEGORY_FORMATTING = "formatting"
    private val CATEGORY_CONNECTION = "connection"

    val relay: RelayOptions
    val connect: ConnectOptions
    val formatting: FormattingOptions

    data class RelayOptions(
            val systemUser: String,
            val deathEvents: Boolean,
            val advancements: Boolean,
            val joinLeave: Boolean
    )

    data class FormattingOptions(
            val chat: String,
            val joinLeave: String,
            val action: String
    )

    data class ConnectOptions(
            val url: String,
            val authToken: String,
            val gateway: String
    )

    init {
        MatterLink.logger.info("Reading bridge blueprints...")
        val config = Configuration()

        config.addCustomCategoryComment(CATEGORY_RELAY_OPTIONS, "Relay options")
        relay = RelayOptions(

                systemUser = config.getString(
                        "systemUser",
                        CATEGORY_RELAY_OPTIONS,
                        "Server",
                        "Name of the server user (used by death and advancement messages and the /say command)"
                ),
                deathEvents = config.getBoolean(
                        "deathEvents",
                        CATEGORY_RELAY_OPTIONS,
                        false,
                        "Relay player death messages"
                ),
                advancements = config.getBoolean(
                        "advancements",
                        CATEGORY_RELAY_OPTIONS,
                        false,
                        "Relay player advancements"
                ),
                joinLeave = config.getBoolean(
                        "joinLeave",
                        CATEGORY_RELAY_OPTIONS,
                        false,
                        "Relay when a player joins or leaves the game"
                )
        )

        config.addCustomCategoryComment(CATEGORY_FORMATTING, "Formatting options: " +
                "Available variables: {username}, {text}, {gateway}, {channel}, {protocol}, {username:antiping}")
        formatting = FormattingOptions(
                chat = config.getString(
                        "chat",
                        CATEGORY_FORMATTING,
                        "<{username}> {text}",
                        "Generic chat event, just talking"
                ),
                joinLeave = config.getString(
                        "joinLeave",
                        CATEGORY_FORMATTING,
                        "§6-- {username} {text}",
                        "Join and leave events from other gateways"
                ),
                action = config.getString(
                        "action",
                        CATEGORY_FORMATTING,
                        "§5* {username} {text}",
                        "User actions (/me) sent by users from other gateways"
                )
        )

        config.addCustomCategoryComment(CATEGORY_CONNECTION, "Connection settings")
        connect = ConnectOptions(
                url = config.getString(
                        "connectURL",
                        CATEGORY_CONNECTION,
                        "http://example.com:1234",
                        "The URL or IP address of the bridge server"
                ),
                authToken = config.getString(
                        "authToken",
                        CATEGORY_CONNECTION,
                        "",
                        "Auth token used to connect to the bridge server"
                ),
                gateway = config.getString(
                        "gateway",
                        CATEGORY_CONNECTION,
                        "minecraft",
                        "MatterBridge gateway"
                )
        )

        if (config.hasChanged()) config.save()

        cfg = this
    }
}