package matterlink.bridge

import matterlink.cfg
import matterlink.antiping
import com.google.gson.Gson

data class ApiMessage(
        val username: String = cfg!!.relay.systemUser,
        val text: String = "",
        val gateway: String = cfg!!.connect.gateway,
        val channel: String = "",
        val userid: String = "",
        val avatar: String = "",
        val account: String = "",
        val event: String = "",
        val protocol: String = "",
//        val timestamp: Date,
        val id: String = ""
//        val Extra: Any? = null
) {
    companion object {
        val gson = Gson()

        fun decode(json: String): ApiMessage {
            return gson.fromJson(json, ApiMessage::class.java)
        }
    }

    fun encode(): String {
        return gson.toJson(this)
    }

    fun format(fmt: String): String {
        var result = fmt
        result = result.helpFormat("{username}", username)
        result = result.helpFormat("{text}", text)
        result = result.helpFormat("{gateway}", gateway)
        result = result.helpFormat("{channel}", channel)
        result = result.helpFormat("{protocol}", protocol)
        result = result.helpFormat("{username:antiping}", username.antiping())
        return result
    }

    private fun String.helpFormat(name: String, value: String): String {
        if (this.contains(name)) {
            return this.replace(name, value)
        }
        return this
    }
}