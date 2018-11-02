package matterlink.bridge.command

import matterlink.config.cfg

object HelpCommand : IBridgeCommand() {
    override val help: String = "Returns the help string for the given command. Syntax: help <command>"
    override val permLevel: Double
        get() = cfg.command.defaultPermUnauthenticated

    override suspend fun execute(alias: String, user: String, env: CommandEnvironment, args: String): Boolean {
        val msg: String = when {
            args.isEmpty() ->
                "Available commands: ${BridgeCommandRegistry.getCommandList(IBridgeCommand.getPermLevel(env.uuid))}"
            else -> args.split(" ", ignoreCase = false)
                .joinToString(separator = "\n") {
                    "$it: ${BridgeCommandRegistry.getHelpString(it)}"
                }
        }
        env.respond(
            text = msg,
            cause = "Help Requested $args"
        )
        return true
    }

}