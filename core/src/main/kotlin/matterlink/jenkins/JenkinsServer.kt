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


class JenkinsServer(val url: String) {

    fun getUrl(job: String) = url + "/job/" + job.replace("/", "/job/")

    fun getJob(job: String, userAgent: String): Job? {
        val requestURL = getUrl(job) + "/api/json"
        val (_, _, result) = requestURL
            .httpGet()
            .header("User-Agent" to userAgent)
            .responseObject(kotlinxDeserializerOf(loader = Job.serializer(), json = JSON.nonstrict))
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