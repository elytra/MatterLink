package matterlink

private const val ZWSP: Char = '\u200b'

//Inserts a zero-width space at index 1 in the string'

fun String.antiping(): String {
    return this[0].toString() + ZWSP + this.substring(1)
}

fun String.mapFormat(env: Map<String, String>): String {
    var result = this
    env.forEach { key, value ->
        if (result.contains(key)) {
            result = result.replace(key, value)
        }
    }
    return result
}
