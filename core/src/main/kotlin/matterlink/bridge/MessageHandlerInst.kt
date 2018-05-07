package matterlink.bridge

import matterlink.antiping
import matterlink.api.ApiMessage
import matterlink.api.MessageHandler
import matterlink.mapFormat

object MessageHandlerInst : MessageHandler()

fun ApiMessage.format(fmt: String): String {
    return fmt.mapFormat(
            mapOf(
                    "{username}" to username,
                    "{text}" to text,
                    "{gateway}" to gateway,
                    "{channel}" to channel,
                    "{protocol}" to protocol,
                    "{username:antiping}" to username.antiping
            )
    )
}