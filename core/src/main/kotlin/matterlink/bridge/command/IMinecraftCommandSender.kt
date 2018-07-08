package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.stripColorOut

abstract class IMinecraftCommandSender(val user: String, val userId: String, val server: String, val uuid: String?, val username: String?, val op: Boolean) {
    /**
     * @param   cmdString The command to execute with its arguments
     *
     * @return  True for success, or false for failure
     */
    abstract fun execute(cmdString: String): Boolean

    val displayName = username ?: user
    val accountName = "$user (id=$userId server=$server uuid=$uuid)"

    fun canExecute(commandName: String): Boolean {
        if (op) return true
        val command = BridgeCommandRegistry[commandName] ?: return false
        return command.canExecute(uuid)
    }

    var reply: String = ""

    fun sendReply(text: String) {
        reply = text
        MessageHandlerInst.transmit(
                ApiMessage(
                        text = text.stripColorOut
                )
        )
    }
}