package matterlink

import com.google.gson.*
import java.lang.reflect.Type

object RegexDeSerializer: JsonSerializer<Regex>, JsonDeserializer<Regex> {
    override fun serialize(src: Regex, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.pattern)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Regex {
        return json.asString.toRegex()
    }

}