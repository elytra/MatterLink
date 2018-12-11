package matterlink.jenkins

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.serialization.kotlinxDeserializerOf
import com.github.kittinunf.result.Result
import kotlinx.serialization.json.JSON
import matterlink.logger


/**
 * Created by nikky on 03/02/18.
 * @author Nikky
 */

//@JsonIgnoreProperties(ignoreUnknown = true)
data class Build(
    val number: Int,
    val url: String
) {
    fun details(userAgent: String): BuildWithDetails? {
        val (request, response, result) = "$url/api/json"
            .httpGet()
            .header("User-Agent" to userAgent)
            .responseObject(kotlinxDeserializerOf(loader = BuildWithDetails.serializer(), json = JSON.nonstrict))
        return when (result) {
            is Result.Success -> {
                result.value
            }
            is Result.Failure -> {
                logger.error(result.error.toString())
                null
            }
        }
    }
}

