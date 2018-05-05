package matterlink.handlers

import matterlink.bridge.MessageHandler
import matterlink.bridge.ServerChatHandler
import matterlink.update.UpdateChecker

/**
 * Created by nikky on 21/02/18.
 * @author Nikky
 * @version 1.0
 */
object TickHandler {
    var tickCounter = 0
        private set
    private var accumulator = 0
    private const val updateInterval = 12 * 60 * 60 * 20
    fun handleTick() {
        tickCounter++
        if (tickCounter % 100 == 0) {
            MessageHandler.checkConnection()
        }

        ServerChatHandler.writeIncomingToChat()

        if (accumulator++ > updateInterval) {
            accumulator -= updateInterval
            UpdateChecker.run()
        }
    }
}