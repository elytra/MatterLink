package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.config.CommandConfig
import matterlink.config.PermissionConfig
import matterlink.config.cfg
import matterlink.instance
import java.util.*

object BridgeCommandRegistry {

    private val commandMap: HashMap<String, IBridgeCommand> = hashMapOf()

    fun handleCommand(input: ApiMessage): Boolean {
        if (!cfg.command.enable || input.text.isBlank()) return false

        if (input.text[0] != cfg.command.prefix || input.text.length < 2) return false

        val cmd = input.text.substring(1).split(' ', ignoreCase = false, limit = 2)
        val args = if (cmd.size == 2) cmd[1] else ""

        return commandMap[cmd[0]]?.execute(cmd[0], input.username, input.userid, input.account, args) ?: false
    }

    fun register(alias: String, cmd: IBridgeCommand): Boolean {
        if (alias.isBlank() || commandMap.containsKey(alias)) {
            instance.error("Failed to register command: '$alias'")
            return false
        }
        if (!cmd.validate()) {
            instance.error("Failed to validate command: '$alias'")
            return false
        }
        //TODO: maybe write alias to command here ?
        // could avoid searching for the command in the registry
        commandMap[alias] = cmd
        return true
    }

    fun getHelpString(cmd: String): String {
        if (!commandMap.containsKey(cmd)) return "No such command."

        val help = commandMap[cmd]!!.help

        return if (help.isNotBlank()) help else "No help for '$cmd'"
    }

    fun getCommandList(permLvl: Double): String {
        return commandMap
                .filterValues {
                    it.permLevel <= permLvl
                }
                .keys
                .joinToString(" ")
    }

    fun reloadCommands() {
        commandMap.clear()
        val permStatus = PermissionConfig.loadPermFile()
        register("help", HelpCommand)
        register("req", PermCommand)
        val cmdStatus = CommandConfig.readConfig()
        CommandConfig.commands.forEach { (alias, command) ->
            register(alias, command)
        }
    }

    operator fun get(command: String) = commandMap[command]

    fun getName(command: IBridgeCommand): String? {
        commandMap.forEach { (alias, cmd) ->
            if (command == cmd) return alias
        }
        return null
    }
}