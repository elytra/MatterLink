package matterlink.handlers

import matterlink.antiping
import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.config.cfg

object ProgressHandler {

    fun handleProgress(name: String, message: String, display: String) {
        if (!cfg.outgoing.advancements) return
        val usr = name.antiping
        MessageHandler.transmit(ApiMessage(
                text = "$usr $message $display"
        ))
    }
}