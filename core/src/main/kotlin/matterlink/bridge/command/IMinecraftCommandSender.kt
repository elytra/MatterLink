package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.stripColorOut

abstract class IMinecraftCommandSender(val user: String, val userId: String, val server: String, val op: Boolean) {
    /**
     * @param   cmdString The command to execute with its arguments
     *
     * @return  True for success, or false for failure
     */
    abstract fun execute(cmdString: String): Boolean

    val accountName = "$user (id=$userId server=$server)"

    fun canExecute(commandName: String): Boolean {
        if(op) return true
        val command = BridgeCommandRegistry[commandName] ?: return false
        return command.canExecute(userId, server)
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