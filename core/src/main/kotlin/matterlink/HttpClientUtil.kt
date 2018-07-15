package matterlink

import matterlink.HttpClientUtil.client
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.HttpClientBuilder


/**
 * Created by nikky on 15/07/18.
 * @author Nikky
 */

object HttpClientUtil {
    val client = HttpClientBuilder.create().build()
}

fun String.httpGet(): HttpGet =
        HttpGet(this)

fun HttpGet.header(pair: Pair<String, String>): HttpGet = this.apply {
    addHeader(pair.first, pair.second)
}

fun HttpGet.responseString(): Triple<HttpRequestBase, HttpResponse, Result> {
    val response = client.execute(this)

    val result = response.entity.content.bufferedReader().use { it.readText() }

    return Triple(this, response, Result.Success(result))
}

sealed class Result {
    class Success(
            val value: String
    ) : Result()

    class Failure(
            val error: Throwable
    ) : Result()
}