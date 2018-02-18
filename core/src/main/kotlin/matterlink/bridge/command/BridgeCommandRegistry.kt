package matterlink.bridge.command

import matterlink.bridge.ApiMessage
import matterlink.config.CommandConfig
import matterlink.config.PermissionConfig
import matterlink.config.cfg
import matterlink.instance
import java.util.*

object BridgeCommandRegistry {

    private val commandMap: HashMap<String, IBridgeCommand> = hashMapOf()

    fun handleCommand(input: ApiMessage): Boolean {
        if (!cfg.command.enable) return false
        if (input.text[0] != cfg.command.prefix[0] || input.text.length < 2) return false

        val cmd = input.text.substring(1).split(' ', ignoreCase = false, limit = 2)
        val args = if (cmd.size == 2) cmd[1] else ""

        return if (commandMap.containsKey(cmd[0]))
            (commandMap[cmd[0]]!!.execute(input.username, input.userid, input.account, args))
        else false
    }

    fun register(cmd: IBridgeCommand): Boolean {
        if (cmd.alias.isBlank() || commandMap.containsKey(cmd.alias)) {
            instance.error("Failed to register command: '${cmd.alias}'")
            return false
        }
        if (!cmd.validate()) {
            instance.error("Failed to validate command: '${cmd.alias}'")
            return false
        }
        commandMap[cmd.alias] = cmd
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

    fun getCommandList(permLvl: Int): String {
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
        register(HelpCommand)
        val cmdStatus = CommandConfig.readConfig()
        registerAll(*CommandConfig.commands)
    }

    operator fun get(command: String) = commandMap[command]
}