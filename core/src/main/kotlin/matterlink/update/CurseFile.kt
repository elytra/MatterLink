package matterlink.update

import com.google.gson.Gson

data class CurseFile(
        val downloadURL: String,
        val fileName: String,
        val gameVersion: List<String>,
        val releaseType: String
) {
    companion object {
        val gson = Gson()

        fun decode(json: String): CurseFile {
            return gson.fromJson(json, CurseFile::class.java)
        }
    }

}