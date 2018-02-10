package matterlink

import matterlink.bridge.MessageHandler
import org.apache.logging.log4j.Logger

lateinit var instance: IMatterLink
lateinit var logger: Logger

abstract class IMatterLink {
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
}