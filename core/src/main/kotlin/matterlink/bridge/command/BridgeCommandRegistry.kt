package matterlink.bridge.command

import matterlink.config.cfg
import matterlink.logger
import java.util.*

object BridgeCommandRegistry {

    private val commandMap: HashMap<String, IBridgeCommand> = HashMap()

    fun handleCommand(input: String): Boolean {
        if (!cfg!!.command.enable) return false
        if (input[0] != cfg!!.command.prefix[0] || input.length < 2) return false

        val cmd = input.substring(1).split(' ', ignoreCase = false, limit = 2)
        val args = if (cmd.size == 2)
            cmd[1]
        else
            ""

        return if (commandMap.containsKey(cmd[0])) (commandMap[cmd[0]]!!.call(args)) else false
    }

    fun register(cmd: IBridgeCommand): Boolean {
        if (cmd.name.isBlank() || commandMap.containsKey(cmd.name)) {
            logger.error("Failed to register command: '${cmd.name}'")
            return false
        }
        commandMap[cmd.name] = cmd
        return true
    }

    fun registerAll(vararg commands: IBridgeCommand) {
        commands.forEach { register(it) }
    }

    fun getHelpString(cmd: String): String {
        if (!commandMap.containsKey(cmd)) return "No such command."

        val help = commandMap[cmd]!!.help

        return if (help.isNotBlank()) help else "No help for '$cmd'"
    }

    val commandList: String
        get() = commandMap.keys.joinToString(separator = ", ")
}