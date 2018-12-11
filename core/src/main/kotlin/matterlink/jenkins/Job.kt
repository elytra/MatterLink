package matterlink.jenkins

import kotlinx.serialization.Serializable

/**
 * Created by nikky on 03/02/18.
 * @author Nikky
 */

@Serializable
data class Job(
    val url: String,
    val name: String,
    val fullName: String,
    val displayName: String,
    val fullDisplayName: String,
    val builds: List<Build>?,
    val lastSuccessfulBuild: Build?,
    val lastStableBuild: Build?
) {
    fun getBuildByNumber(build: Int, userAgent: String): BuildWithDetails? {
        return builds?.find { it.number == build }?.details(userAgent)
    }
}