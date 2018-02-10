package matterlink.config

var cfg: IMatterLinkConfig? = null

abstract class IMatterLinkConfig {
    protected val CATEGORY_RELAY_OPTIONS = "relay"
    protected val CATEGORY_FORMATTING_INCOMING = "formatting"
    protected val CATEGORY_FORMATTING_JOIN_LEAVE = "formatting_join_leave"
    protected val CATEGORY_CONNECTION = "connection"
    protected val CATEGORY_COMMAND = "command"

    var relay: RelayOptions = RelayOptions()
    var connect: ConnectOptions = ConnectOptions()
    var formatting: FormattingOptions = FormattingOptions()
    var formattingJoinLeave: FormattingJoinLeave = FormattingJoinLeave()
    var command: CommandOptions = CommandOptions()

    data class RelayOptions(
            val systemUser: String = "Server",
            val deathEvents: Boolean = true,
            val advancements: Boolean = true,
            val joinLeave: Boolean = true
    )

    data class FormattingOptions(
            val chat: String = "<{username}> {text}",
            val joinLeave: String = "§6-- {username} {text}",
            val action: String = "§5* {username} {text}"
    )

    data class FormattingJoinLeave(
            val joinServer: String = "{username:antiping} has connected to the server",
            val leaveServer: String = "{username:antiping} has disconnected from the server"
    )

    data class ConnectOptions(
            val url: String = "http://localhost:4242",
            val authToken: String = "",
            val gateway: String = "minecraft"
    )

    data class CommandOptions(
            val prefix: String = "$",
            val enable: Boolean = true
    )
}