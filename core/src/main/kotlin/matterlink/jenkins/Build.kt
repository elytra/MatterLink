package matterlink.jenkins

import com.google.gson.Gson
import matterlink.*
import matterlink.Result
import matterlink.header
import matterlink.httpGet
import matterlink.responseString

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
                .responseString()
        return when(result) {
            is Result.Success -> {
                gson.fromJson(result.value, BuildWithDetails::class.java)
            }
            is Result.Failure -> {
                logger.error(result.error.toString())
                null
            }
        }
    }

    companion object {
        val gson = Gson()
    }
}

