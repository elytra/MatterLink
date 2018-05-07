package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst

abstract class IMinecraftCommandSender(val user: String, val userId: String, val server: String) {
    /**
     * @param   cmdString The command to execute with its arguments
     *
     * @return  True for success, or false for failure
     */
    abstract fun execute(cmdString: String): Boolean

    val accountName = "$user (id=$userId server=$server)"

    fun canExecute(commandName: String): Boolean {
        val command = BridgeCommandRegistry[commandName] ?: return false

        return command.canExecute(userId, server)
    }

    var reply: String = ""

    fun sendReply(text: String) {
        reply = text
        MessageHandlerInst.transmit(ApiMessage()
                .setText(text)
        )
    }
}