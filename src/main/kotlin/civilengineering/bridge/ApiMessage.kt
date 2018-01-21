package civilengineering.bridge

import civilengineering.Config
import com.google.gson.Gson

data class ApiMessage(
        val username: String = "",
        val text: String = "",
        val gateway: String = Config.gateway,
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
    fun encode(): String {
        return gson.toJson(this)
    }

    companion object {
        val gson = Gson()

        fun decode(json: String): ApiMessage {
            return gson.fromJson(json, ApiMessage::class.java)
        }
    }
}