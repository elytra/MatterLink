package matterlink.bridge

import matterlink.config.cfg
import matterlink.antiping
import com.google.gson.Gson
import matterlink.mapFormat

const val USER_ACTION: String = "user_action"
const val JOIN_LEAVE: String = "join_leave"

data class ApiMessage(
        val username: String = cfg.relay.systemUser,
        val text: String = "",
        val gateway: String = cfg.connect.gateway,
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
        return fmt.mapFormat(
                mapOf(
                        "{username}" to username,
                        "{text}" to text,
                        "{gateway}" to gateway,
                        "{channel}" to channel,
                        "{protocol}" to protocol,
                        "{username:antiping}" to username.antiping()
                )
        )

    }
}