package matterlink.update

import com.google.gson.Gson
import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.config.cfg
import matterlink.instance
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import java.io.BufferedReader

class UpdateChecker : Runnable {

    override fun run() {
        val gson = Gson()

        val currentModVersion = instance.modVersion
        val currentMCVersion = instance.mcVersion
        val currentVersion = currentMCVersion + "-" + currentModVersion

        instance.info("Checking for new versions...")

        val client: HttpClient = HttpClients.createDefault()
        val response: HttpResponse = client.execute(HttpGet("http://bit.ly/matterlinkfiles"))
        val apiUpdateList = if (200 == response.statusLine.statusCode) { //HTTP 200 OK
            val buffer: BufferedReader = response.entity.content.bufferedReader()

            //put all of the buffer content onto the string
            val content = buffer.readText()
            instance.trace("updateData: $content")

            gson.fromJson(content, Array<CurseFile>::class.java)
                    .filter {
                        it.gameVersion.contains(currentMCVersion)
                    }
                    .sortedByDescending { it.fileName.substringAfterLast(" ") }

        } else {
            instance.error("Could not check for updates!")
            return
        }

        val possibleUpdates = mutableListOf<CurseFile>()
        apiUpdateList.forEach {
            instance.debug(it.toString())
            val version = it.fileName.substringAfter("-")
            if(version > currentModVersion)
            {
                possibleUpdates += it
            }
        }
        if(possibleUpdates.isEmpty()) return
        val latest= possibleUpdates[0]

        possibleUpdates.sortByDescending { it.fileName.substringAfter(" ") }
        val version = if(possibleUpdates.count() == 1) "version" else "versions"
        instance.info("Matterlink out of date! You are {} $version behind", possibleUpdates.count())
        possibleUpdates.forEach {
            instance.info("version: {} download: {}", it.fileName, it.downloadURL)
        }

        instance.warn("Mod out of date! New $version available at ${latest.downloadURL}")
        MessageHandler.transmit(ApiMessage(
                username = cfg.relay.systemUser,
                text = "Matterlink out of date! You are {} $version behind! Please download new version from ${latest.downloadURL}"
        ))
    }

}