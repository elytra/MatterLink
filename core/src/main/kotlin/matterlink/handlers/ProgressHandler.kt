package matterlink.handlers

import matterlink.antiping
import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.config.cfg
import matterlink.stripColorOut

object ProgressHandler {

    fun handleProgress(name: String, message: String, display: String) {
        if (!cfg.outgoing.advancements) return
        val usr = name.stripColorOut.antiping
        MessageHandlerInst.transmit(
                ApiMessage(
                        text = "$usr $message $display".stripColorOut
                )
        )
    }
}