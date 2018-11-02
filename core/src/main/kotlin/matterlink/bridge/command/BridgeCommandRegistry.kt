package matterlink.bridge.command

import matterlink.api.ApiMessage
import matterlink.config.CommandConfig
import matterlink.config.IdentitiesConfig
import matterlink.config.PermissionConfig
import matterlink.config.cfg
import matterlink.logger
import java.util.HashMap
import java.util.UUID

object BridgeCommandRegistry {

    private val commandMap: HashMap<String, IBridgeCommand> = hashMapOf()

    /**
     *
     * @return consume message flag
     */
    suspend fun handleCommand(input: ApiMessage): Boolean {
        if (!cfg.command.enable || input.text.isBlank()) return false

        if (input.text[0] != cfg.command.prefix || input.text.length < 2) return false

        val cmd = input.text.substring(1).split(' ', ignoreCase = false, limit = 2)
        val args = if (cmd.size == 2) cmd[1] else ""

        val uuid = IdentitiesConfig.getUUID(input.account, input.userid)

        val env = IBridgeCommand.CommandEnvironment.BridgeEnv(
            input.username,
            input.userid,
            input.account,
            input.gateway,
            uuid
        )
        return commandMap[cmd[0]]?.let {
            if (!it.reachedTimeout()) {
                logger.debug("dropped command ${it.alias}")
                return false
            }
            it.preExecute() // resets the tickCounter
            if (!it.canExecute(uuid)) {
                env.respond(
                    text = "${input.username} is not permitted to perform command: ${cmd[0]}"
                )
                return false
            }
            it.execute(cmd[0], input.username, env, args)
        } ?: false
    }

    suspend fun handleCommand(text: String, username: String, uuid: UUID): Boolean {
        if (!cfg.command.enable || text.isBlank()) return false

        if (text[0] != cfg.command.prefix || text.length < 2) return false

        val cmd = text.substring(1).split(' ', ignoreCase = false, limit = 2)
        val args = if (cmd.size == 2) cmd[1] else ""

        val env = IBridgeCommand.CommandEnvironment.GameEnv(username, uuid)

        return commandMap[cmd[0]]?.let {
            if (!it.reachedTimeout()) {
                logger.debug("dropped command ${it.alias}")
                return false
            }
            it.preExecute() // resets the tickCounter
            if (!it.canExecute(uuid)) {
                env.respond(
                    text = "$username is not permitted to perform command: ${cmd[0]}"
                )
                return false
            }

            it.execute(cmd[0], username, env, args)
        } ?: false
    }

    fun register(alias: String, cmd: IBridgeCommand): Boolean {
        if (alias.isBlank() || commandMap.containsKey(alias)) {
            logger.error("Failed to register command: '$alias'")
            return false
        }
        if (!cmd.validate()) {
            logger.error("Failed to validate command: '$alias'")
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
        register("help", HelpCommand)
        if (cfg.command.authRequests)
            register("auth", AuthBridgeCommand)
        if (cfg.command.permisionRequests)
            register("request", RequestPermissionsCommand)
        PermissionConfig.loadFile()
        CommandConfig.loadFile()
        IdentitiesConfig.loadFile()

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