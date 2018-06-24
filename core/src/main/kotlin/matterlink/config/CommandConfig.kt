package matterlink.config

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonObject
import blue.endless.jankson.JsonPrimitive
import blue.endless.jankson.impl.Marshaller
import blue.endless.jankson.impl.SyntaxError
import matterlink.bridge.command.CommandType
import matterlink.bridge.command.CustomCommand
import matterlink.instance
import java.io.File
import java.io.FileNotFoundException

typealias CommandMap = MutableMap<String, CustomCommand>
typealias DefaultCommands = Map<String, Pair<String, CustomCommand>>

object CommandConfig {
    private val configFile: File = cfg.cfgDirectory.resolve("commands.json")

    private val default: DefaultCommands = mapOf(
            "tps" to ("""Your run off the mill tps commands, change it to /sampler tps or /cofh tps if you like
                |make sure to disable defaultCommand if you want your edits to have any effect
            """.trimMargin()
                    to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "forge tps",
                    help = "Print server tps",
                    timeout = 200,
                    defaultCommand = true
            )),
            "list" to ("lists all the players, this is just a straight pass-through"
                    to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "list",
                    help = "List online players",
                    defaultCommand = true
            )),
            "seed" to ("another straight pass-through"
                    to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "seed",
                    help = "Print server world seed",
                    defaultCommand = true
            )),
            "uptime" to ("this is a reponse command, it uses the uptime function, time since the mod was first loaded"
                    to CustomCommand(
                    type = CommandType.RESPONSE,
                    response = "{uptime}",
                    help = "Print server uptime",
                    defaultCommand = true
            )),
            "whoami" to ("this shows you some of the other response macros"
                    to CustomCommand(
                    type = CommandType.RESPONSE,
                    response = "server: `{server}` userid: `{userid}` user: `{user}`",
                    help = "Print debug user data",
                    timeout = 200,
                    defaultCommand = true
            )),
            "exec" to ("this uses arguments in a passed-through command, you could restrict the arguments with a regex"
                    to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "{args}",
                    argumentsRegex = ".*".toRegex(),
                    permLevel = 1.0,
                    help = "Execute any command as OP, be careful with this one",
                    execOp = true,
                    defaultCommand = true
            ))
    )

    val commands: CommandMap = hashMapOf()

    fun readConfig(): Boolean {
        val jankson = Jankson
                .builder()
                .registerTypeAdapter(CustomCommand::class.java) { jsonObj ->
                    with(CustomCommand.DEFAULT) {
                        CustomCommand(
                                type = jsonObj.get(CommandType::class.java, "type") ?: type,
                                execute = jsonObj.get(String::class.java, "execute") ?: execute,
                                response = jsonObj.get(String::class.java, "response") ?: response,
                                permLevel = jsonObj.get(Double::class.java, "permLevel") ?: permLevel,
                                help = jsonObj.get(String::class.java, "help") ?: help,
                                timeout = jsonObj.get(Int::class.java, "timeout") ?: timeout,
                                defaultCommand = jsonObj.get(Boolean::class.java, "defaultCommand") ?: defaultCommand,
                                execOp = jsonObj.get(Boolean::class.java, "execOp") ?: execOp,
                                argumentsRegex = jsonObj.get(Regex::class.java, "argumentsRegex") ?: argumentsRegex
                        )
                    }
                }
                .registerPrimitiveTypeAdapter(CommandType::class.java) {jsonObj ->
                    CommandType.valueOf(jsonObj.toString())
                }
                .registerPrimitiveTypeAdapter(Regex::class.java) {jsonObj ->
                    jsonObj.toString().toRegex()
                }
                .build()

        Marshaller.getFallback().registerSerializer(Regex::class.java) {
            JsonPrimitive(it.pattern)
        }
        Marshaller.getFallback().registerSerializer(CommandType::class.java) {
            JsonPrimitive(it.name)
        }

        val jsonObject = try {
            jankson.load(configFile)
        } catch (e: SyntaxError) {
            instance.error("error parsing config: ${e.completeMessage}")
            JsonObject()
        } catch (e: FileNotFoundException) {
            configFile.createNewFile()
            JsonObject()
        }
        // clear commands
        commands.clear()
        jsonObject.forEach { key, element ->
            instance.trace("loading command '$key'")
            val command = jankson.fromJson(element.toJson(), CustomCommand::class.java)
            commands[key] = command
        }

        //apply defaults
        default.forEach { k, (comment, defCommand) ->
            val command = commands[k]
            if (command == null || command.defaultCommand == true) {
                commands[k] = defCommand
                val element = Marshaller.getFallback().serialize(defCommand)
                jsonObject.putDefault(k, element, comment)
            }
        }

        instance.debug("loaded jsonObj: $jsonObject")
        instance.debug("loaded commandMap: $commands")

        configFile.writeText(jsonObject.toJson(true, true))

        return true
    }


}