package matterlink.bridge.command

interface IMinecraftCommandSender {
    /**
     * @param   cmdString The command to execute with its arguments
     *
     * @return  True for success, or false for failure
     */
    fun execute(cmdString: String) : Boolean
}