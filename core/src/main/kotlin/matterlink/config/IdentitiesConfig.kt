package matterlink.config

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonObject
import blue.endless.jankson.impl.Marshaller
import blue.endless.jankson.impl.SyntaxError
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import matterlink.getList
import matterlink.logger
import matterlink.stackTraceString
import java.io.File
import java.io.FileNotFoundException
import java.util.UUID
import java.util.concurrent.TimeUnit

typealias IdentMap = Map<String, Map<String, List<String>>>

data class AuthRequest(
    val username: String,
    val uuid: String,
    val nonce: String,
    val platform: String,
    val userid: String
)

object IdentitiesConfig {
    val authRequests: Cache<String, AuthRequest> = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build()

    private val jankson = Jankson
        .builder()
        .build()

    private val configFile: File = baseCfg.cfgDirectory.resolve("identities.hjson")

    private val default = mapOf(
        ("edd31c45-b095-49c5-a9f5-59cec4cfed8c" to mapOf(
            "discord.game" to (listOf("112228624366575616") to "discord id")
        ) to "username: NikkyAi")
    )

    var idents: IdentMap = mapOf()
        private set

    private var jsonObject: JsonObject = JsonObject()

    fun loadFile() {

        val defaultJsonObject = JsonObject().apply {
            default.forEach { (uuid, userMap), uuidComment ->
                val jsonUserMap = this.putDefault(uuid, JsonObject(), uuidComment)
                if (jsonUserMap is JsonObject) {
                    userMap.forEach { platform, (user, comment) ->
                        logger.trace("loading uuid: $uuid platform: $platform user: $user")
                        val element = Marshaller.getFallback().serialize(user)
                        jsonUserMap.putDefault(platform, element, comment.takeUnless { it.isBlank() })
                    }
                    this[uuid] = jsonUserMap
                } else {
                    logger.error("cannot parse uuid: $uuid , value: '$jsonUserMap' as Map, skipping")
                }
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
        val tmpIdents: MutableMap<String, Map<String, List<String>>> = mutableMapOf()
        jsonObject.forEach { uuid, jsonIdentifier ->
            val identMap: MutableMap<String, List<String>> = tmpIdents[uuid]?.toMutableMap() ?: mutableMapOf()
            if (jsonIdentifier is JsonObject) {
                jsonIdentifier.forEach { platform, user ->
                    logger.info("$uuid $platform $user")
                    identMap[platform] = jsonIdentifier.getList(platform) ?: emptyList()
                }
            }
            tmpIdents[uuid] = identMap.toMap()
        }
        idents = tmpIdents.toMap()

        logger.info("Identities loaded")

        if (save)
            configFile.writeText(jsonObject.toJson(true, true))
    }

    fun add(uuid: String, username: String, platform: String, userid: String, comment: String? = null) {
        val platformObject = jsonObject.getObject(uuid) ?: JsonObject()
        platformObject.putDefault(platform, userid, comment)
        val userIdList = platformObject.getList<String>(platform) ?: emptyList()
        platformObject[platform] = platformObject.marshaller.serialize(userIdList + userid)
        jsonObject[uuid] = platformObject

        if (jsonObject.getComment(uuid) == null) {
            jsonObject.setComment(uuid, "Username: $username")
        }

        load()
    }

    //TODO: rewrite, store ident map differently in memory
    fun getUUID(platform: String, userid: String): UUID? {
        return idents.entries.firstOrNull { (uuid, usermap) ->
            usermap.entries.any { (_platform, userids) ->
                if (platform.equals(_platform, true))
                    logger.info("platform: $_platform userids: $userids")
                platform.equals(_platform, true) && userids.contains(userid)
            }
        }?.key?.let {
            try {
                UUID.fromString(it)
            } catch (e: IllegalArgumentException) {
                logger.error("cannot parse UUID: $it")
                logger.error(e.stackTraceString)
                null
            }
        }
    }
}