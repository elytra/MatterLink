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

typealias CommandMap = MutableMap<String, CustomCommand>

object CommandConfig {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File = cfg.cfgDirectory.resolve("commands.json")

    private val default = hashMapOf(
            "tps" to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "forge tps",
                    help = "Print server tps",
                    allowArgs = false,
                    timeout = 200,
                    defaultCommand = true
            ),
            "list" to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "list",
                    help = "List online players",
                    allowArgs = false,
                    defaultCommand = true
            ),
            "seed" to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "seed",
                    help = "Print server world seed",
                    allowArgs = false,
                    defaultCommand = true
            ),
            "uptime" to CustomCommand(
                    type = CommandType.RESPONSE,
                    permLevel = 1.0,
                    response = "{uptime}",
                    help = "Print server uptime",
                    allowArgs = false,
                    defaultCommand = true
            ),
            "whoami" to CustomCommand(
                    type = CommandType.RESPONSE,
                    response = "server: `{server}` userid: `{userid}` user: `{user}`",
                    help = "Print debug user data",
                    allowArgs = false,
                    timeout = 200,
                    defaultCommand = true
            ),
            "exec" to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "",
                    permLevel = 1.0,
                    help = "Execute any command as OP, be careful with this one",
                    allowArgs = false,
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
            return true
        }

        try {
            commands = gson.fromJson(configFile.readText(), object : TypeToken<CommandMap>() {}.type)
            commands.filterValues { it.defaultCommand ?: false }.forEach { commands.remove(it.key) }
            default.forEach { k, v ->
                if(!commands.containsKey(k)){
                    commands[k] = v
                }
            }
            configFile.writeText(gson.toJson(commands))
        } catch (e: JsonSyntaxException) {
            instance.fatal(e.stackTraceString)
            instance.fatal("failed to parse $configFile using last good values as fallback")

            return false
        }
        return true
    }


}