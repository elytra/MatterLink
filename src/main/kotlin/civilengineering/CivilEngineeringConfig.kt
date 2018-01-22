package civilengineering

import net.minecraftforge.common.config.Configuration
import java.io.File

var cfg: CivilEngineeringConfig? = null

class CivilEngineeringConfig(file: File) {
    private val CATEGORY_RELAY_OPTIONS = "relay"
    private val CATEGORY_CONNECTION = "connection"

    val relay: RelayOptions
    val connect: ConnectOptions

    data class RelayOptions(
            val deathEvents: Boolean,
            val advancements: Boolean,
            val joinLeave: Boolean
    )

    data class ConnectOptions(
            val url: String,
            val authToken: String,
            val gateway: String
    )

    init {
        CivilEngineering.logger.info("Reading bridge blueprints...")
        val config = Configuration(file.resolve("CivilEngineering.cfg"))

        config.addCustomCategoryComment(CATEGORY_RELAY_OPTIONS, "Relay options")
        config.addCustomCategoryComment(CATEGORY_CONNECTION, "Connection settings")

        relay = RelayOptions(
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
                        "Relay player advancements [NOT IMPLEMENTED]"
                ),
                joinLeave = config.getBoolean(
                        "joinLeave",
                        CATEGORY_RELAY_OPTIONS,
                        false,
                        "Relay when a player joins or leaves the game [NOT IMPLEMENTED]"
                )
        )

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