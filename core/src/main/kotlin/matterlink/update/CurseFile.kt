package matterlink.update

data class CurseFile(
    val downloadURL: String,
    val fileName: String,
    val gameVersion: List<String>,
    val releaseType: String,
    val fileStatus: String
)