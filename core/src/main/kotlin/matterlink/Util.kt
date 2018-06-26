package matterlink

import blue.endless.jankson.JsonObject
import matterlink.config.cfg
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

val String.stripColorOut: String
    get() =
        if (cfg.outgoing.stripColors)
            this.replace("§.?".toRegex(RegexOption.UNIX_LINES), "")
        else
            this


val String.stripColorIn: String
    get() = if (cfg.incoming.stripColors)
        this.replace("§.?".toRegex(), "")
    else
        this


val Exception.stackTraceString: String
    get() {
        val sw = StringWriter()
        this.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }

fun <T : Any> JsonObject.getOrDefault(key: String, default: T, comment: String? = null): T {
    instance.info("type: ${default.javaClass.name} key: $key json: >>>${this.getObject(key)?.toJson()}<<< default: $default")
    putDefault(key, default, comment)!!
    return get(default.javaClass, key)!!
}
