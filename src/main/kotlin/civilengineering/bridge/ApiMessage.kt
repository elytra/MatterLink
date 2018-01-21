package civilengineering.bridge

import com.google.gson.Gson

data class ApiMessage(
        val text: String = "",
        val channel: String = "",
        val username: String = "",
        val userid: String = "",
        val avatar: String = "",
        val account: String = "",
        val event: String = "",
        val protocol: String = "",
        val gateway: String = "",
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