package matterlink

import matterlink.bridge.MessageHandler
import org.apache.logging.log4j.Logger

lateinit var instance: IMatterLink
lateinit var logger: Logger

abstract class IMatterLink {
    var interrupted: Boolean = false

    abstract fun wrappedSendToPlayers(msg: String)

    abstract fun wrappedPlayerList(): Array<String>

    fun connect() {
        if (MessageHandler.start(clear = true)) {
            logger.info("Connected to matterbridge relay")
        } else {
            logger.error("Connection to matterbridge relay failed.")
        }
    }

    fun disconnect  () {
        MessageHandler.stop()
    }


    fun reconnect(tick: Int) {
        if(tick % 20 == 0  && interrupted) {
            logger.info("Trying to reconnect")
            if (MessageHandler.start(clear = false)) {
                logger.info("Reconnected to matterbridge relay")
            } else {
                logger.error("Reconnection to matterbridge relay failed.")
            }
        }
    }
}