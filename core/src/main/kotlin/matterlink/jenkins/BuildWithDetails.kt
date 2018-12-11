package matterlink.jenkins

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class BuildWithDetails(
    val number: Int,
    val url: String,
    val artifacts: List<Artifact>,
    val timestamp: Date
)