package matterlink.config

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonObject
import blue.endless.jankson.impl.Marshaller
import blue.endless.jankson.impl.SyntaxError
import matterlink.getOrDefault
import matterlink.instance
import java.io.File
import java.io.FileNotFoundException

typealias PermissionMap = MutableMap<String, MutableMap<String, Double>>

data class PermissionRequest(
        val user: String,
        val platform: String,
        val userId: String,
        val powerlevel: Double? = null
)

object PermissionConfig {
    val permissionRequests = mutableMapOf<String, PermissionRequest>()
    private val jankson = Jankson
            .builder()
            .build()

    private val configFile: File = baseCfg.cfgDirectory.resolve("permissions.hjson")

    private val default = mapOf(
            "irc.esper" to mapOf(
                    "~nikky@nikky.moe" to (0.0 to "IRC users are identified by their username and hostmask"),
                    "user@example.com" to (0.0 to "")
            ),
            "discord.game" to mapOf(
                    "112228624366575616" to (0.0 to "thats a discord user id")
            )
    )

    val perms: PermissionMap = mutableMapOf()
    private var jsonObject: JsonObject = JsonObject()

    fun loadPermFile(): Boolean {
        permissionRequests.clear()

        jsonObject = try {
            jankson.load(configFile)
        } catch (e: SyntaxError) {
            instance.error("error parsing config: ${e.completeMessage}")
            JsonObject()
        } catch (e: FileNotFoundException) {
            configFile.createNewFile()
            JsonObject()
        }

        default.forEach { platform, userMap ->
            val jsonUserMap = jsonObject.getOrDefault(platform, JsonObject())
            if(jsonUserMap is JsonObject) {
                userMap.forEach { user, (powerlevel, comment) ->
                    instance.trace("loading platform: $platform user: $user powerlevel: $powerlevel")
                    val element = Marshaller.getFallback().serialize(powerlevel)
                    jsonUserMap.putDefault(user, element, comment.takeUnless { it.isBlank() })
                }
                jsonObject[platform] = jsonUserMap
            } else {
                instance.error("cannot parse platform: $platform , value: '$jsonUserMap' as Map, skipping")
            }
        }

        jsonObject.forEach { platform, jsonUserMap ->
            val userMap = perms[platform] ?: mutableMapOf()
            if (jsonUserMap is JsonObject) {
                jsonUserMap.forEach { user, powerlevel ->
                    instance.info("$platform $user $powerlevel")
                    userMap[user] = jsonUserMap.get(Double::class.java, user) ?: 0.0
                }
            }
            perms[platform] = userMap
        }

        configFile.writeText(jsonObject.toJson(true, true))

        return true
    }

    fun add(platform: String, userid: String, powerlevel: Double, comment: String? = null) {
        val platformObject = jsonObject.getObject(platform) ?: JsonObject()
        platformObject.getOrDefault(userid, powerlevel, comment)
        jsonObject[platform] = platformObject

        perms.clear()
        jsonObject.forEach { platform, jsonUserMap ->
            val userMap = perms[platform] ?: mutableMapOf()
            if (jsonUserMap is JsonObject) {
                jsonUserMap.forEach { user, powerlevel ->
                    instance.info("$platform $user $powerlevel")
                    userMap[user] = jsonUserMap.get(Double::class.java, user) ?: 0.0
                }
            }
            perms[platform] = userMap
        }
        instance.info("Permissions reloaded")

        configFile.writeText(jsonObject.toJson(true, true))
    }
}