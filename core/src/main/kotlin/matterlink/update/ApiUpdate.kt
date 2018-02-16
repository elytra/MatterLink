package matterlink.update

import com.google.gson.Gson

data class ApiUpdate (
        val downloadURL: String,
        val fileName: String,
        val gameVersion: Array<String>,
        val releaseType: String
) {
    companion object {
        val gson = Gson()

        fun decode(json: String): ApiUpdate {
            return gson.fromJson(json, ApiUpdate::class.java)
        }
    }

}