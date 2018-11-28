package matterlink.update

import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.config.cfg
import matterlink.handlers.ChatEvent
import matterlink.handlers.LocationHandler
import matterlink.instance
import matterlink.jenkins.JenkinsServer
import matterlink.logger
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker : CoroutineScope {
    override val coroutineContext = Job() + CoroutineName("UpdateChacker")

    suspend fun check() {
        if (cfg.update.enable) {
            run()
        }
    }

    private suspend fun run() {
        if (instance.buildNumber > 0) {
            val server = JenkinsServer("https://ci.elytradev.com")
            val job = server.getJob("elytra/MatterLink/master", "MatterLink/${instance.modVersion}")
                ?: run {
                    logger.error("failed obtaining job: elytra/MatterLink/master")
                    return
                }
            //TODO: add job name to constants at build time
            val build = job.lastSuccessfulBuild ?: run {
                logger.error("no successful build found")
                return
            }
            with(build) {
                when {
                    number > instance.buildNumber -> {
                        logger.warn("Mod out of date! New build $number available at $url")
                        val difference = number - instance.buildNumber
                        LocationHandler.sendToLocations(
                            msg = "MatterLink out of date! You are $difference builds behind! Please download new version from $url",
                            x = 0, y = 0, z = 0, dimension = null,
                            event = ChatEvent.STATUS,
                            cause = "MatterLink update notice"
                        )
                    }
                    number < instance.buildNumber -> logger.error("lastSuccessfulBuild: $number is older than installed build: ${instance.buildNumber}")
                    else -> logger.info("you are up to date")
                }
            }
            return
        }
        if (instance.modVersion.contains("-dev")) {
            logger.debug("Not checking updates on developer build")
            return
        }

        val gson = GsonBuilder()
//                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()

        logger.info("Checking for new versions...")

        val url = URL("https://staging_cursemeta.dries007.net/api/v3/direct/addon/287323/files")
        val con = url.openConnection() as HttpURLConnection

        with(instance) {
            val useragent =
                "MatterLink/$modVersion MinecraftForge/$mcVersion-$forgeVersion (https://github.com/elytra/MatterLink)"
            logger.debug("setting User-Agent: '$useragent'")
            con.setRequestProperty("User-Agent", useragent)
        }

        con.connect()

        val apiUpdateList = if (200 == con.responseCode) { //HTTP 200 OK
            val buffer: BufferedReader = con.inputStream.bufferedReader()

            //put all of the buffer content onto the string
            val content = buffer.readText()
            logger.trace("updateData: $content")

            gson.fromJson(content, Array<CurseFile>::class.java)
                .filter {
                    it.fileStatus == "SemiNormal" && it.gameVersion.contains(instance.mcVersion)
                }
                .sortedByDescending { it.fileName.substringAfterLast(" ") }

        } else {
            logger.error("Could not check for updates!")
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
            logger.debug(it.toString())
            val version = it.fileName.substringAfterLast("-").split('.').map { it.toInt() }
            var bigger = false
            version.forEachIndexed { index, chunk ->
                if (!bigger) {
                    val currentChunk = modVersionChunks.getOrNull(index) ?: 0
                    logger.debug("$chunk > $currentChunk")
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

        logger.info("Matterlink out of date! You are $count $version behind")
        possibleUpdates.forEach {
            logger.info("version: ${it.fileName} download: ${it.downloadURL}")
        }

        logger.warn("Mod out of date! New $version available at ${latest.downloadURL}")
        MessageHandlerInst.transmit(
            ApiMessage(
                text = "MatterLink out of date! You are $count $version behind! Please download new version from ${latest.downloadURL}"
            )
        )
    }
}