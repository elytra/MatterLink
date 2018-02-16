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
import java.util.regex.Pattern

class UpdateChecker : Runnable {
    override fun run() {
        instance.info("Checking for new versions...")

        val ApiUpdateList : Array<ApiUpdate>

        val client : HttpClient = HttpClients.createDefault()
        val response : HttpResponse = client.execute(HttpGet("https://cursemeta.nikky.moe/api/addon/287323/files"))
        if (200 == response.statusLine.statusCode) { //HTTP 200 OK
            val buffer : BufferedReader = response.entity.content.bufferedReader()

            //put all of the buffer content onto the string
            var content : String = ""
            var line : String? = buffer.readLine()
            while (line != null) {
                instance.debug(line)
                content += line
                line = buffer.readLine()
            }
            instance.debug("updateData: $content")

            val gson = Gson()
            ApiUpdateList = gson.fromJson<Array<ApiUpdate>>(content,Array<ApiUpdate>::class.java)
        } else {
            instance.error("Could not check for updates!")
            return
        }

        val possibleUpdates = HashMap<String,ApiUpdate>()
        var maxVersion : String = ""
        ApiUpdateList.forEach {
            //TODO: fix this if we ever release jars that support multiple versions
            if (it.gameVersion[0] == instance.mcVersion) {
                if (Pattern.matches("[mM]atter[lL]ink \\d+\\.\\d+\\.\\d+-\\d+\\.\\d+\\.?\\d*",it.fileName)                        ) {
                    val version : String = it.fileName.split("-")[1]
                    instance.debug(version)
                    possibleUpdates.set(version,it)
                    if (version.compareTo(maxVersion)>0 || maxVersion.equals("")) maxVersion = version
                }
            }
        }

        if (maxVersion.isEmpty()) return

        if (maxVersion.compareTo(instance.modVersion)<0) {
            val latest : ApiUpdate? = possibleUpdates[maxVersion]
            if (latest != null) {
                instance.warn("Mod out of date! New version available at ${latest.downloadURL}")
                MessageHandler.transmit(ApiMessage(
                        username = cfg!!.relay.systemUser,
                        text = "Matterlink out of date! Please download new version from ${latest.downloadURL}"
                ))
            } else {
                instance.fatal("Severe error in update checker!")
            }
        }
    }

}