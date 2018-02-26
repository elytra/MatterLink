package matterlink.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import io.github.config4k.extract
import io.github.config4k.toConfig
import matterlink.bridge.command.CommandType
import matterlink.bridge.command.CustomCommand
import matterlink.instance
import matterlink.stackTraceString
import java.io.File

object CommandConfig {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File = cfg.cfgDirectory.resolve("commands.hocon")

    private val default = hashMapOf(
            "tps" to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "forge tps",
                    help = "Print server tps",
                    allowArgs = false
            ),
            "list" to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "list",
                    help = "List online players",
                    allowArgs = false
            ),
            "seed" to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "seed",
                    help = "Print server world seed",
                    allowArgs = false
            ),
            "uptime" to CustomCommand(
                    type = CommandType.RESPONSE,
                    permLevel = 1,
                    response = "{uptime}",
                    help = "Print server uptime",
                    allowArgs = false
            )
    )

    var commands: Map<String, CustomCommand> = default
        private set

    val options = ConfigRenderOptions
            .defaults()
            .setJson(false)
            .setOriginComments(false)

    fun readConfig(): Boolean {
        if (!configFile.exists()) {
            configFile.createNewFile()
            val config: Config = default.toConfig("commands")
            configFile.writeText(config.root().render(options))
            return true
        }

        var success = true
        commands = try {
            val config = ConfigFactory.parseFile(configFile)
            val commandConfig = config.getConfig("commands")

            val keys = config.getObject("commands")
                    .unwrapped()
                    .keys

            keys.associate { it to commandConfig.extract<CustomCommand>(it) }

        } catch (e: ConfigException) {
            instance.fatal(e.stackTraceString)
            instance.fatal("failed to parse $configFile using last good values as fallback")

            success = false
            commands
        }
        return success
    }


}