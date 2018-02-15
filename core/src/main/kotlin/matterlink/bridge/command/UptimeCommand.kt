package matterlink.bridge.command

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.config.cfg
import matterlink.instance

object UptimeCommand : IBridgeCommand {
    override val name: String = "uptime"
    override val help: String = "Get server uptime."

    override fun call(args: String): Boolean {
        if (args.isNotBlank()) return false
        MessageHandler.transmit(ApiMessage(
                username = cfg!!.relay.systemUser,
                text = instance.getUptimeAsString()
        ))
        return true
    }

}