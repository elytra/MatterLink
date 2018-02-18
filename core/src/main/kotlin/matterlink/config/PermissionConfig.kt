package matterlink.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

object PermissionConfig {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File = cfg.cfgDirectory.resolve("permissions.json")

    private val default = hashMapOf<String,HashMap<String,Int>>()

    var perms : HashMap<String,HashMap<String,Int>> = default

    fun loadPermFile() {
        if(!configFile.exists()) {
            configFile.createNewFile()
            perms = default
            configFile.writeText(gson.toJson(default))
            return
        }

        val text = configFile.readText()
        perms = gson.fromJson(text, object : TypeToken <HashMap<String,HashMap<String,Int>>>(){}.type)
    }
}