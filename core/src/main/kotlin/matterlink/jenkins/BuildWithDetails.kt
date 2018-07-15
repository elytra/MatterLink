package matterlink.jenkins

import java.util.*

//@JsonIgnoreProperties(ignoreUnknown = true)
data class BuildWithDetails(
        val number: Int,
        val url: String,
        val artifacts: List<Artifact>,
//        @JsonFormat(shape=JsonFormat.Shape.NUMBER, pattern="s")
        val timestamp: Date
)