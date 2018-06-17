package matterlink.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import matterlink.RegexDeSerializer
import matterlink.bridge.command.CommandType
import matterlink.bridge.command.CustomCommand
import matterlink.instance
import matterlink.stackTraceString
import java.io.File
import java.util.regex.PatternSyntaxException

typealias CommandMap = MutableMap<String, CustomCommand>

object CommandConfig {
    private val gson: Gson = GsonBuilder()
            .registerTypeAdapter(Regex::class.java, RegexDeSerializer)
            .setPrettyPrinting()
            .create()
    private val configFile: File = cfg.cfgDirectory.resolve("commands.json")

    private val default = hashMapOf(
            "tps" to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "forge tps",
                    help = "Print server tps",
                    timeout = 200,
                    defaultCommand = true
            ),
            "list" to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "list",
                    help = "List online players",
                    defaultCommand = true
            ),
            "seed" to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "seed",
                    help = "Print server world seed",
                    defaultCommand = true
            ),
            "uptime" to CustomCommand(
                    type = CommandType.RESPONSE,
                    response = "{uptime}",
                    help = "Print server uptime",
                    defaultCommand = true
            ),
            "whoami" to CustomCommand(
                    type = CommandType.RESPONSE,
                    response = "server: `{server}` userid: `{userid}` user: `{user}`",
                    help = "Print debug user data",
                    timeout = 200,
                    defaultCommand = true
            ),
            "exec" to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "{args}",
                    argumentsRegex = ".*".toRegex(),
                    permLevel = 1.0,
                    help = "Execute any command as OP, be careful with this one",
                    execOp = true,
                    defaultCommand = true
            )
    )

    var commands: CommandMap = default
        private set

    fun readConfig(): Boolean {
        if (!configFile.exists() || configFile.readText().isBlank()) {
            configFile.createNewFile()
            configFile.writeText(gson.toJson(default))
            commands = default
            return true
        }

        try {
            commands = gson.fromJson(configFile.readText(), object : TypeToken<CommandMap>() {}.type)
            commands.filterValues { it.defaultCommand ?: false }.forEach { commands.remove(it.key) }
            default.forEach { k, v ->
                if (!commands.containsKey(k)) {
                    commands[k] = v
                }
            }
            configFile.writeText(gson.toJson(commands))
        } catch (e: JsonSyntaxException) {
            instance.fatal(e.stackTraceString)
            instance.fatal("failed to parse $configFile, using last good values as fallback")

            return false
        } catch (e: PatternSyntaxException) {
            instance.fatal(e.stackTraceString)
            instance.fatal("failed to parse regex in $configFile, using last good values as fallback")

            return false
        }
        return true
    }


}