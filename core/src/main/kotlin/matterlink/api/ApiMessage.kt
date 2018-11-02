package matterlink.api

import kotlinx.serialization.Encoder
import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.JSON

/**
 * Created by nikky on 07/05/18.
 *
 * @author Nikky
 * @version 1.0
 */
@Serializable
data class ApiMessage(
    @Optional var username: String = "",
    @Optional var text: String = "",
    @Optional var gateway: String = "",
    @Optional var timestamp: String = "",
    @Optional var channel: String = "",
    @Optional var userid: String = "",
    @Optional var avatar: String = "",
    @Optional var account: String = "",
    @Optional var protocol: String = "",
    @Optional var event: String = "",
    @Optional var id: String = "",
    @Optional var Extra: Map<String, String>? = null
) {

    fun encode(): String {
        return JSON.stringify(Companion, this)
    }

    override fun toString(): String = encode()

    @Serializer(forClass = ApiMessage::class)
    companion object {
        override fun serialize(output: Encoder, obj: ApiMessage) {
            val elemOutput = output.beginStructure(descriptor)
            obj.username.takeIf { it.isNotEmpty() }?.let {
                elemOutput.encodeStringElement(descriptor, 0, it)
            }
            obj.text.takeIf { it.isNotEmpty() }?.let {
                elemOutput.encodeStringElement(descriptor, 1, it)
            }
            obj.gateway.takeIf { it.isNotEmpty() }?.let {
                elemOutput.encodeStringElement(descriptor, 2, it)
            }
            obj.timestamp.takeIf { it.isNotEmpty() }?.let {
                elemOutput.encodeStringElement(descriptor, 3, it)
            }
            obj.channel.takeIf { it.isNotEmpty() }?.let {
                elemOutput.encodeStringElement(descriptor, 4, it)
            }
            obj.userid.takeIf { it.isNotEmpty() }?.let {
                elemOutput.encodeStringElement(descriptor, 5, it)
            }
            obj.avatar.takeIf { it.isNotEmpty() }?.let {
                elemOutput.encodeStringElement(descriptor, 6, it)
            }
            obj.account.takeIf { it.isNotEmpty() }?.let {
                elemOutput.encodeStringElement(descriptor, 7, it)
            }
            obj.protocol.takeIf { it.isNotEmpty() }?.let {
                elemOutput.encodeStringElement(descriptor, 8, it)
            }
            obj.event.takeIf { it.isNotEmpty() }?.let {
                elemOutput.encodeStringElement(descriptor, 9, it)
            }
            obj.id.takeIf { it.isNotEmpty() }?.let {
                elemOutput.encodeStringElement(descriptor, 10, it)
            }
//            obj.Extra.takeIf { ! it.isNullOrEmpty() }?.let {
//                elemOutput.encodeStringElement(descriptor, 11, it)
//            }
            elemOutput.endStructure(descriptor)
        }

        val USER_ACTION = "user_action"
        val JOIN_LEAVE = "join_leave"


        fun decode(json: String): ApiMessage {
            return JSON.parse(Companion, json)
        }
    }
}
