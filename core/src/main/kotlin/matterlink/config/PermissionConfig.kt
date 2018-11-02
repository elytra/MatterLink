package matterlink.config

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonObject
import blue.endless.jankson.impl.SyntaxError
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import matterlink.getReified
import matterlink.logger
import java.io.File
import java.io.FileNotFoundException
import java.util.UUID
import java.util.concurrent.TimeUnit

typealias PermissionMap = Map<String, Double>

data class PermissionRequest(
    val uuid: UUID,
    val user: String,
    val nonce: String,
    val powerlevel: Double? = null
)

object PermissionConfig {
    val permissionRequests: Cache<String, PermissionRequest> = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build()
    private val jankson = Jankson
        .builder()
        .build()

    private val configFile: File = baseCfg.cfgDirectory.resolve("permissions.hjson")

    private val default = mapOf(
        "edd31c45-b095-49c5-a9f5-59cec4cfed8c" to 9000.0 to "Superuser"
    )

    val perms: PermissionMap = mutableMapOf()
    private var jsonObject: JsonObject = JsonObject()

    fun loadFile() {
        val defaultJsonObject = JsonObject().apply {
            default.forEach { (uuid, level), comment ->
                jsonObject.putDefault(uuid, level, comment)
            }
        }

        var save = true
        jsonObject = try {
            jankson.load(configFile)
        } catch (e: SyntaxError) {
            logger.error("error parsing config: ${e.completeMessage}")
            save = false
            defaultJsonObject
        } catch (e: FileNotFoundException) {
            logger.error("cannot find config: $configFile .. creating sample permissions mapping")
            configFile.createNewFile()
            defaultJsonObject
        }

        load(save)
    }

    private fun load(save: Boolean = true) {
        val tmpPerms = mutableMapOf<String, Double>()
        for ((uuid, powerlevel) in jsonObject) {
            val tmpLevel = jsonObject.getReified<Double>(uuid)
            if (tmpLevel == null) {
                logger.warn("cannot parse permission uuid: $uuid level: $powerlevel")
                continue
            }
            tmpPerms[uuid] = tmpLevel
        }

        logger.info("Permissions reloaded")

        if (save)
            configFile.writeText(jsonObject.toJson(true, true))
    }

    fun add(uuid: UUID, powerlevel: Double, comment: String? = null) {
        jsonObject.putDefault(uuid.toString(), powerlevel, comment)
        load()
    }
}