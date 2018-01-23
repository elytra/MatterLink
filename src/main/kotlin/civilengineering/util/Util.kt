package civilengineering.util


object Util {
    const val ZWSP: Char = '\u200b'

    //Inserts a zero-width space at index 1 in the string'

    fun String.antiping(): String {
        return this[0].toString()+ ZWSP +this.substring(1)
    }

}