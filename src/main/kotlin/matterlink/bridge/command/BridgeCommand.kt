package matterlink.bridge.command

class BridgeCommand(val name: String, command: (String) -> Boolean) {
    private val execute: (String) -> Boolean = command //return true for success and false for failure

    fun tryExecute(input: String): Boolean {
        //get the first word
        val space = input.indexOf(' ')
        if (space == 0) return false //"! " is never a command

        var cmd = if (space > 0) input.substring(0, space) else input

        return if (cmd == name) execute(input.substring(space + 1)) else false
    }
}