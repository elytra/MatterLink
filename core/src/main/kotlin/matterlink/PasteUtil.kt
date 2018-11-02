package matterlink

import blue.endless.jankson.Jankson
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by nikky on 09/07/18.
 * @author Nikky
 */

data class Paste(
    val encrypted: Boolean = false,
    val description: String,
    val sections: List<PasteSection>
)

data class PasteSection(
    val name: String,
    val syntax: String = "text",
    val contents: String
)

data class PasteResponse(
    val id: String,
    val link: String
)

object PasteUtil {
    private const val DEFAULT_KEY = "uKJoyicVJFnmpnrIZMklOURWxrCKXYaiBWOzPmvon"

    private val jankson = Jankson.builder()
        .registerTypeAdapter {
            PasteResponse(
                id = it.getReified("id") ?: "",
                link = it.getReified<String>("link")
                    ?.replace("\\/", "/")
                    ?: "invalid"
            )
        }
//            .registerSerializer { paste: Paste, marshaller: Marshaller ->
//                JsonObject().apply {
//                    with(paste) {
//                        if (description.isNotBlank())
//                            this@apply["description"] = marshaller.serialize(description)
//                        if (encrypted)
//                            this@apply["encrypted"] = marshaller.serialize(encrypted)
//                        this@apply["sections"] = marshaller.serialize(sections)
//                    }
//                }
//            }
//            .registerSerializer { section: PasteSection, marshaller: Marshaller ->
//                JsonObject().apply {
//                    with(section) {
//                        if (name.isNotBlank())
//                            this@apply["name"] = marshaller.serialize(name)
//                        this@apply["syntax"] = marshaller.serialize(syntax)
//                        this@apply["contents"] = marshaller.serialize(contents.replace("\n", "\\n"))
//                    }
//                }
//            }
        .build()

    fun paste(paste: Paste, key: String = ""): PasteResponse {
        val apiKey = key.takeIf { it.isNotBlank() } ?: DEFAULT_KEY

        val url = URL("https://api.paste.ee/v1/pastes")
        val http = url.openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.doOutput = true

        val out = jankson.toJson(paste)
            .toJson(false, false)
            .toByteArray()

        http.setFixedLengthStreamingMode(out.size)
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        http.setRequestProperty("X-Auth-Token", apiKey)
        http.connect()
        http.outputStream.use { os ->
            os.write(out)
        }

        val textResponse = http.inputStream.bufferedReader().use { it.readText() }
        logger.debug("response: $textResponse")
//        val jsonObject = jankson.load(http.inputStream)
        return jankson.fromJson(textResponse)
    }
}