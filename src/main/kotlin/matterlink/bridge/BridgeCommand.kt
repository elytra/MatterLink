package matterlink.bridge

import matterlink.cfg

class BridgeCommand(val name: String, command: (List<String>) -> Boolean) {
    val execute: (List<String>) -> Boolean = command //return true for success and false for failure

    companion object Handler {
        private val commandMap: HashMap<String, BridgeCommand> = HashMap()

        fun handleCommand(input: String): Boolean {
            if (input[0] != cfg!!.command.prefix[0]) return false

            val cmd = ArrayList(input.substring(1).split(" "))
            val args = cmd.subList(1, cmd.size - 1)

            return if (commandMap.containsKey(cmd[0])) (commandMap[cmd[0]]!!.execute)(args) else false
        }

        fun registerCommand(cmd: BridgeCommand): Boolean {
            if (cmd.name.isBlank() || commandMap.containsKey(cmd.name)) return false
            commandMap[cmd.name] = cmd
            return true
        }
    }
}