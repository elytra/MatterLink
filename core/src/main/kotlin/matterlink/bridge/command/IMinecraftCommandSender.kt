package matterlink.bridge.command

import matterlink.instance

abstract class IMinecraftCommandSender(val user: String, val userId: String, val server: String) {
    /**
     * @param   cmdString The command to execute with its arguments
     *
     * @return  True for success, or false for failure
     */
    abstract fun execute(cmdString: String): Boolean

    val accountName = "$user (id=$userId server=$server)"

    fun canExecute(commandName: String) : Boolean {
        instance.info("testing $commandName")
        val command = BridgeCommandRegistry[commandName] ?: return false

        return command.canExecute(userId, server)
    }
}