package matterlink

import java.io.PrintWriter
import java.io.StringWriter

private const val ZWSP: Char = '\u200b'

//Inserts a zero-width space at index 1 in the string'

val String.antiping: String
    get() {
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

fun String.lazyFormat(env: Map<String, () -> String>): String {
    var result = this
    env.forEach { key, value ->
        if (result.contains(key)) {
            result = result.replace(key, value())
        }
    }
    return result
}

val Exception.stackTraceString: String
    get() {
        val sw = StringWriter()
        this.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }