package matterlink.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.MalformedJsonException
import matterlink.instance
import java.io.File

typealias PermissionMap = HashMap<String, HashMap<String, Int>>

object PermissionConfig {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File = cfg.cfgDirectory.resolve("permissions.json")

    private val default = PermissionMap()

    var perms: PermissionMap = default

    fun loadPermFile(): Boolean {
        if (!configFile.exists()) {
            configFile.createNewFile()
            perms = default
            configFile.writeText(gson.toJson(default))
            return true
        }

        val text = configFile.readText()
        try {
            perms = gson.fromJson(text, object : TypeToken<PermissionMap>() {}.type)
        } catch (e: JsonSyntaxException) {
            instance.fatal("failed to parse $configFile using last good values as fallback")
            return false
        }
        return true
    }
}