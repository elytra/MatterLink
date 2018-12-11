package matterlink.jenkins

import kotlinx.serialization.Serializable

/**
 * Created by nikky on 03/02/18.
 * @author Nikky
 */

@Serializable
data class Artifact(
    val displayPath: String,
    val fileName: String,
    val relativePath: String
)