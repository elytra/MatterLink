package matterlink

import matterlink.bridge.MessageHandler
//import org.apache.logging.log4j.Logger

lateinit var instance: IMatterLink
//lateinit var logger: Logger

abstract class IMatterLink {
    var interrupted: Boolean = false

    abstract fun wrappedSendToPlayers(msg: String)

    abstract fun wrappedPlayerList(): Array<String>

    fun connect() {
        if (MessageHandler.start(clear = true)) {
            println("Connected to matterbridge relay")
        } else {
            System.err.println("Connection to matterbridge relay failed.")
        }
    }

    fun disconnect  () {
        MessageHandler.stop()
    }


    fun reconnect(tick: Int) {
        if(tick % 20 == 0  && interrupted) {
            println("Trying to reconnect")
            if (MessageHandler.start(clear = false)) {
                println("Reconnected to matterbridge relay")
            } else {
                System.err.println("Reconnection to matterbridge relay failed.")
            }
        }
    }
}