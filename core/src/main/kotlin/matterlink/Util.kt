package matterlink

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonArray
import blue.endless.jankson.JsonElement
import blue.endless.jankson.JsonObject
import blue.endless.jankson.impl.Marshaller
import matterlink.config.cfg
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import kotlin.streams.asSequence

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

fun String.lazyFormat(env: Map<String, () -> String?>): String {
    var result = this
    env.forEach { key, value ->
        if (result.contains(key)) {
            result = result.replace(key, value().toString())
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

fun randomString(length: Long = 6, source: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ") = Random().ints(length, 0, source.length)
        .asSequence()
        .map(source::get)
        .joinToString("")

fun <T : Any> JsonObject.getOrDefault(key: String, default: T, comment: String? = null): T {
//    instance.info("type: ${default.javaClass.name} key: $key json: >>>${this.getObject(key)?.toJson()}<<< default: $default")
    return putDefault(key, default, comment)!!
}

inline fun <reified T : Any> Jankson.fromJson(obj: JsonObject): T = this.fromJson(obj, T::class.java)
inline fun <reified T : Any> Jankson.fromJson(json: String): T = this.fromJson(json, T::class.java)

inline fun <reified T : Any> Jankson.Builder.registerTypeAdapter(noinline adapter: (JsonObject) -> T) = this.registerTypeAdapter(T::class.java, adapter)

inline fun <reified T : Any> Jankson.Builder.registerPrimitiveTypeAdapter(noinline adapter: (Any) -> T) = this.registerPrimitiveTypeAdapter(T::class.java, adapter)

inline fun <reified T : Any> Jankson.Builder.registerSerializer(noinline serializer: (T, Marshaller) -> JsonElement) = this.registerSerializer(T::class.java, serializer)

inline fun <reified T : Any> Marshaller.registerSerializer(noinline serializer: (T) -> JsonElement) = this.registerSerializer(T::class.java, serializer)

inline fun <reified T : Any> Marshaller.registerSerializer(noinline serializer: (T, Marshaller) -> JsonElement) = this.registerSerializer(T::class.java, serializer)

inline fun <reified T : Any> JsonObject.getReified(key: String): T? = this.get(T::class.java, key)

inline fun <reified T : Any> JsonObject.getList(key: String): List<T>? {
    return this[key]?.let { array ->
        when (array) {
            is JsonArray -> {
                array.indices.map { i ->
                    array.get(T::class.java, i) ?: throw NullPointerException("cannot parse ${array.get(i)}")
                }
            }
            else -> null
        }
    }
}