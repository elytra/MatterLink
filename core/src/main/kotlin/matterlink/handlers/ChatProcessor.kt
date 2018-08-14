package matterlink.handlers

import matterlink.api.ApiMessage
import matterlink.bridge.MessageHandlerInst
import matterlink.bridge.command.BridgeCommandRegistry
import matterlink.config.cfg
import matterlink.logger
import matterlink.stripColorOut
import java.util.*

object ChatProcessor {
    /**
     * @return cancel message flag
     */
    fun sendToBridge(user: String, msg: String, x: Int, y: Int, z: Int, dimension: Int?, event: ChatEvent, uuid: UUID? = null): Boolean {
        //TODO: pass message to Locations
        logger.info("position: $x $y $z dimension: $dimension")
        val message = msg.trim()
        if (uuid != null && BridgeCommandRegistry.handleCommand(message, user, uuid)) return true
        when {
            message.isNotBlank() -> LocationHandler.sendToLocations(
                    user = user,
                    msg = message,
                    x = x, y = y, z = z, dimension = dimension,
                    event = event,
                    cause = "Message from $user"
            )


            else -> logger.warn("WARN: dropped blank message by '$user'")
        }
        return false
    }
}
