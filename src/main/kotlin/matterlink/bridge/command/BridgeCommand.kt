package matterlink.bridge.command

import matterlink.MatterLink
import matterlink.cfg
import java.util.regex.Pattern

class BridgeCommand(val name: String, command: (String) -> Boolean, val help: String) {
    val execute: (String) -> Boolean = command //return true for success and false for failure

    companion object Handler {
        private val commandMap: HashMap<String, BridgeCommand> = HashMap()

        fun handleCommand(input: String): Boolean {
            if (!cfg!!.command.enable) return false
            if (input[0] != cfg!!.command.prefix[0] || input.length < 2) return false

            val cmd = input.substring(1).split(delimiters = ' ', ignoreCase = false, limit = 2)
            val args = if (cmd.size > 1) cmd[1] else ""

            return if (commandMap.containsKey(cmd[0])) (commandMap[cmd[0]]!!.execute)(args) else false
        }

        fun register(cmd: BridgeCommand): Boolean {
            if (cmd.name.isBlank() || commandMap.containsKey(cmd.name)) return false
            commandMap[cmd.name] = cmd
            return true
        }

        fun registerAll(vararg cmds: BridgeCommand) {
            for (cmd in cmds) {
                if (!register(cmd)) MatterLink.logger.error("Failed to register command: " + cmd.name)
            }
        }

        fun getHelpString(cmd: String): String {
            if (!commandMap.containsKey(cmd)) return "No such command."

            val help = commandMap[cmd]!!.help

            return if (help.isNotBlank()) help else "No help for command " + cfg!!.command.prefix + cmd
        }

        fun listCommands(): String {
            return commandMap.keys.joinToString(" | ")
        }
    }
}