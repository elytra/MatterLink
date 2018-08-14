package matterlink.handlers

import matterlink.antiping
import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.config.cfg
import matterlink.stripColorOut

object ProgressHandler {

    fun handleProgress(name: String, message: String, display: String,
                       x: Int, y: Int, z: Int,
                       dimension: Int) {
        if (!cfg.outgoing.advancements) return
        val usr = name.stripColorOut.antiping
        LocationHandler.sendToLocations(
                msg = "$usr $message $display".stripColorOut,
                x = x, y = y, z = z, dimension = dimension,
                event = ChatEvent.ADVANCEMENT,
                cause = "Progress Event by $usr",
                systemuser = true
        )
    }
}