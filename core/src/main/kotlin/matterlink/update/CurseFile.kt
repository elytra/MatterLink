package matterlink.update

import kotlinx.serialization.Serializable

@Serializable
data class CurseFile(
    val downloadUrl: String,
    val fileName: String,
    val fileNameOnDisk: String,
    val gameVersion: List<String>,
    val releaseType: String,
    val fileStatus: String
)