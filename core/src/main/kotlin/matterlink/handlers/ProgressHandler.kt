package matterlink.handlers

import matterlink.antiping
import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.config.cfg

object ProgressHandler {

    fun handleProgress(name: String, message: String, display: String) {
        if (!cfg.outgoing.advancements) return
        val usr = name.antiping
        MessageHandlerInst.transmit(ApiMessage()
                .setText("$usr $message $display")
        )
    }
}