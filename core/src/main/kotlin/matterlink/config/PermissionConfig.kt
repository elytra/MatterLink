package matterlink.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import matterlink.instance
import matterlink.stackTraceString
import java.io.File

typealias PermissionMap = Map<String, Map<String, Int>>

object PermissionConfig {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File = cfg.cfgDirectory.resolve("permissions.json")

    private val default = mapOf(
            "irc.esper" to mapOf(
                    "~nikky@nikky.moe" to 0,
                    "user@example." to 0
            ),
            "discord.game" to mapOf(
                    "112228624366575616" to 0
            )
    )

    var perms: PermissionMap = default

    fun loadPermFile(): Boolean {
        if (!configFile.exists() || configFile.readText().isBlank()) {
            configFile.createNewFile()
            configFile.writeText(gson.toJson(default))
            return true
        }

        try {
            perms =gson.fromJson(configFile.readText(), object : TypeToken<PermissionMap>() {}.type)
        } catch (e: JsonSyntaxException) {
            instance.fatal(e.stackTraceString)
            instance.fatal("failed to parse $configFile using last good values as fallback")

            return true
        }
        return false
    }
}