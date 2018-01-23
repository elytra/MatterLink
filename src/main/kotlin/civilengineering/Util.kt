package civilengineering

class Util {
    companion object {
        const val ZWSP: Char = '\u200b'

        //Inserts a zero-width space at index 1 in the string'
        @JvmStatic
        fun antiping(str: String): String {
            return str[0].toString()+ZWSP+str.substring(1)
        }
    }
}