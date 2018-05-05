package matterlink.update

import com.google.gson.Gson

data class CurseFile(
        val downloadURL: String,
        val fileName: String,
        val gameVersion: List<String>,
        val releaseType: String,
        val fileStatus: String
)