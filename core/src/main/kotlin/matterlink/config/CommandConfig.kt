package matterlink.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import matterlink.bridge.command.CommandType
import matterlink.bridge.command.CustomCommand
import matterlink.instance
import matterlink.stackTraceString
import java.io.File

typealias CommandMap = Map<String, CustomCommand>

object CommandConfig {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File = cfg.cfgDirectory.resolve("commands.json")

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

    var commands: CommandMap = default
        private set

    fun readConfig(): Boolean {
        if (!configFile.exists() || configFile.readText().isBlank()) {
            configFile.createNewFile()
            configFile.writeText(gson.toJson(default))
            return true
        }

        try {
            commands = gson.fromJson(configFile.readText(), object : TypeToken<CommandMap>() {}.type)
        } catch (e: JsonSyntaxException) {
            instance.fatal(e.stackTraceString)
            instance.fatal("failed to parse $configFile using last good values as fallback")

            return false
        }
        return true
    }


}