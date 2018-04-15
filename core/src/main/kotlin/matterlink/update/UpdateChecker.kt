package matterlink.update

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.config.cfg
import matterlink.instance
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import java.io.BufferedReader

class UpdateChecker : Thread() {
    companion object {
        fun run() {
            if (cfg.update.enable) {
                UpdateChecker().start()
            }
        }
    }

    init {
        name = "UpdateCheckerThread"
    }

    override fun run() {
        if (instance.modVersion.contains("-build")) {
            instance.debug("Not checking updates on Jenkins build")
            return
        }
        if (instance.modVersion.contains("-dev")) {
            instance.debug("Not checking updates on developer build")
            return
        }

        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create()

        instance.info("Checking for new versions...")

        val client = HttpClients.createDefault()
        val request = HttpGet("https://cursemeta.dries007.net/api/v2/direct/GetAllFilesForAddOn/287323")

        with(instance) {
            val useragent = "MatterLink/$modVersion MinecraftForge/$mcVersion-$forgeVersion (https://github.com/elytra/MatterLink)"
            instance.debug("setting User-Agent: '$useragent'")
            request.setHeader("User-Agent", useragent)
        }

        val response: HttpResponse = client.execute(request)
        val apiUpdateList = if (200 == response.statusLine.statusCode) { //HTTP 200 OK
            val buffer: BufferedReader = response.entity.content.bufferedReader()

            //put all of the buffer content onto the string
            val content = buffer.readText()
            instance.trace("updateData: $content")

            gson.fromJson(content, Array<CurseFile>::class.java)
                    .filter {
                        it.fileStatus == "SemiNormal" && it.gameVersion.contains(instance.mcVersion)
                    }
                    .sortedByDescending { it.fileName.substringAfterLast(" ") }

        } else {
            instance.error("Could not check for updates!")
            return
        }

        val modVersionChunks = instance.modVersion
                .substringBefore("-dev")
                .substringBefore("-build")
                .split('.')
                .map {
                    it.toInt()
                }

        val possibleUpdates = mutableListOf<CurseFile>()
        apiUpdateList.forEach {
            instance.debug(it.toString())
            val version = it.fileName.substringAfterLast("-").split('.').map { it.toInt() }
            var bigger = false
            version.forEachIndexed { index, chunk ->
                if (!bigger) {
                    val currentChunk = modVersionChunks.getOrNull(index) ?: 0
                    instance.debug("$chunk > $currentChunk")
                    if (chunk < currentChunk)
                        return@forEach

                    bigger = chunk > currentChunk
                }
            }
            if (bigger) {
                possibleUpdates += it
            }
        }
        if (possibleUpdates.isEmpty()) return
        val latest = possibleUpdates[0]

        possibleUpdates.sortByDescending { it.fileName.substringAfter(" ") }
        val count = possibleUpdates.count()
        val version = if (count == 1) "version" else "versions"

        instance.info("Matterlink out of date! You are $count $version behind")
        possibleUpdates.forEach {
            instance.info("version: {} download: {}", it.fileName, it.downloadURL)
        }

        instance.warn("Mod out of date! New $version available at ${latest.downloadURL}")
        MessageHandler.transmit(ApiMessage(
                username = cfg.outgoing.systemUser,
                text = "MatterLink out of date! You are $count $version behind! Please download new version from ${latest.downloadURL}"
        ))
    }
}