package matterlink.bridge

import matterlink.*
import matterlink.api.ApiMessage
import matterlink.api.MessageHandler
import matterlink.config.cfg

object MessageHandlerInst : MessageHandler() {
    override fun transmit(msg: ApiMessage) {
        transmit(msg, cause = "")
    }

    fun transmit(msg: ApiMessage, cause: String, maxLines: Int = cfg.outgoing.inlineLimit) {
        if (msg.username.isEmpty()) {
            msg.username = cfg.outgoing.systemUser

            if(msg.avatar.isEmpty() && cfg.outgoing.avatar.enable) {
                msg.avatar = cfg.outgoing.avatar.systemUserAvatar
            }
        }
        if (msg.gateway.isEmpty())
            msg.gateway = cfg.connect.gateway

        if (msg.text.lines().count() >= maxLines) {
            try {
                val response = PasteUtil.paste(
                        Paste(
                                description = cause,
                                sections = listOf(
                                        PasteSection(
                                                name = "log.txt",
                                                syntax = "text",
                                                contents = msg.text
                                        )
                                )
                        )
                )
                msg.text = msg.text.substringBefore('\n')
                        .take(25) + "...  " + response.link
            } catch(e: Exception) {
                logger.error(e.stackTraceString)
            }
        }
        super.transmit(msg)
    }
}

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