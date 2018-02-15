package matterlink.command

interface IMinecraftCommandSender {
    /**
     * @param   cmdString The command to execute with its arguments
     * @param   level     Privilege level to execute this command at, currently unused
     *
     * @return  Any output of the command
     */
    fun execute(cmdString: String, level: Int) : Boolean
}