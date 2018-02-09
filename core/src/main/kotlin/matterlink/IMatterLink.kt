package matterlink

import matterlink.bridge.MessageHandler
import matterlink.bridge.ServerChatHandler
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.message.SimpleMessageFactory
import org.apache.logging.log4j.simple.SimpleLogger
import org.apache.logging.log4j.util.PropertiesUtil
import java.util.*

var instance: IMatterLink = DummyLink()

//create fake logger to get around Nullability
var logger: Logger = SimpleLogger("",
        Level.OFF,
        false,
        false,
        false,
        false,
        "",
        SimpleMessageFactory(),
        PropertiesUtil(Properties()),
        System.out)

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


class DummyLink : IMatterLink() {
    override fun wrappedPlayerList(): Array<String> {
        TODO("not implemented, make sure the MatterLink implementations is loaded") //To change body of created functions use File | Settings | File Templates.
    }

    override fun wrappedSendToPlayers(msg: String) {
        TODO("not implemented, make sure the MatterLink implementations is loaded") //To change body of created functions use File | Settings | File Templates.
    }

}
