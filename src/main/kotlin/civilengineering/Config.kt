package civilengineering

import net.minecraftforge.common.config.Configuration
import org.apache.logging.log4j.Level

object Config {
    private val CATEGORY_RELAY_OPTIONS = "relay_options"
    private val CATEGORY_CONNECTION = "connection"

    var relayDeathEvents = false
    var relayAdvancements = false //unused for now
    var relayJoinLeave = false

    var connectURL = "http://localhost"
    var authToken = ""
    var gateway = ""

    fun readConfig() {

        val config = CivilEngineering.config
        try {
            config.load()
            initConfig(config)
        } catch (e: Exception) {
            CivilEngineering.logger!!.log(Level.ERROR, "Could not read config file!", e)
        } finally {
            if (config.hasChanged()) {
                config.save()
            }
        }
    }

    private fun initConfig(cfg: Configuration) {
        cfg.addCustomCategoryComment(CATEGORY_RELAY_OPTIONS, "Relay options")
        cfg.addCustomCategoryComment(CATEGORY_CONNECTION, "Connection settings")
        relayDeathEvents = cfg.getBoolean(
                "relayDeathEvents",
                CATEGORY_RELAY_OPTIONS,
                false,
                "Relay player death messages"
        )
        relayAdvancements = cfg.getBoolean(
                "relayAdvancements",
                CATEGORY_RELAY_OPTIONS,
                false,
                "Relay player advancements [NOT IMPLEMENTED]"
        )
        relayJoinLeave = cfg.getBoolean(
                "relayJoinLeave",
                CATEGORY_RELAY_OPTIONS,
                false,
                "Relay when a player joins or leaves the game [NOT IMPLEMENTED]"
        )

        connectURL = cfg.getString(
                "connectURL",
                CATEGORY_CONNECTION,
                "http://example.com:1234",
                "The URL or IP address of the bridge server, ex. http://example.com:1234"
        )
        authToken = cfg.getString(
                "auth_token",
                CATEGORY_CONNECTION,
                "",
                "Auth token used to connect to the bridge server"
        )
        gateway = cfg.getString(
                "gateway",
                CATEGORY_CONNECTION,
                "",
                "MatterBridge gateway"
        )

    }

}