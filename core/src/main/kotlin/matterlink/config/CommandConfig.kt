package matterlink.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import matterlink.bridge.command.CommandType
import matterlink.bridge.command.CustomCommand
import java.io.File

object CommandConfig {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File = cfg.cfgDirectory.resolve("commands.json")
    private val default = arrayOf(
            CustomCommand(
                    alias = "tps",
                    type = CommandType.PASSTHROUGH,
                    execute = "forge tps",
                    help = "Print server tps",
                    allowArgs = false
            ),
            CustomCommand(
                    alias = "list",
                    type = CommandType.PASSTHROUGH,
                    execute = "list",
                    help = "List online players",
                    allowArgs = false
            ),
            CustomCommand(
                    alias = "seed",
                    type = CommandType.PASSTHROUGH,
                    execute = "seed",
                    help = "Print server world seed",
                    allowArgs = false
            )
    )

    fun readConfig() : Array<CustomCommand> {
        if(!configFile.exists()) {
            configFile.createNewFile()
            configFile.writeText(gson.toJson(default))
            return default
        }

        val text = configFile.readText()
        return gson.fromJson(text, Array<CustomCommand>::class.java)
    }


}