package voodoo.util.jenkins


import com.google.gson.Gson
import matterlink.Result
import matterlink.header
import matterlink.httpGet
import matterlink.responseString
import matterlink.jenkins.Job
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
                .responseString()
        return when (result) {
            is Result.Success -> {
                gson.fromJson(result.value, Job::class.java)
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