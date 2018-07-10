package matterlink.bridge.command

import matterlink.stripColorOut

abstract class IMinecraftCommandSender(val user: String, val env: IBridgeCommand.CommandEnvironment, val op: Boolean) {
    /**
     * @param   cmdString The command to execute with its arguments
     *
     * @return  True for success, or false for failure
     */
    abstract fun execute(cmdString: String): Boolean

    val displayName = env.username ?: user
    val accountName = when (env) {
        is IBridgeCommand.CommandEnvironment.BridgeEnv -> "$user (id=${env.userId} platform=${env.platform}${env.uuid?.let { " uuid=$it" }
                ?: ""}${env.username?.let { " username=$it" } ?: ""})"
        is IBridgeCommand.CommandEnvironment.GameEnv -> "$user (username=${env.username} uuid=${env.uuid})"
    }

    fun canExecute(commandName: String): Boolean {
        if (op) return true
        val command = BridgeCommandRegistry[commandName] ?: return false
        return command.canExecute(env.uuid)
    }

    private var finished = true
    val reply = mutableListOf<String>()

    /**
     * accumulates response
     */
    fun appendReply(text: String) {
        if (finished) {
            reply.clear()
            finished = false
        }
        reply += text
    }

    fun sendReply(cmdString: String) {
        env.respond(
                text = reply.joinToString("\n"),
                cause = "executed command: $cmdString"
        )
        finished = true
    }
}