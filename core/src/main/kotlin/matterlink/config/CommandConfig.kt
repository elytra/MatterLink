package matterlink.config

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonObject
import blue.endless.jankson.JsonPrimitive
import blue.endless.jankson.impl.SyntaxError
import matterlink.*
import matterlink.bridge.command.CommandType
import matterlink.bridge.command.CustomCommand
import java.io.File
import java.io.FileNotFoundException

typealias CommandMap = MutableMap<String, CustomCommand>
typealias DefaultCommands = Map<String, Pair<String, CustomCommand>>

object CommandConfig {
    private val configFile: File = baseCfg.cfgDirectory.resolve("commands.hjson")

    private val default: DefaultCommands = mapOf(
            "tps" to ("""Your run off the mill tps commands, change it to /sampler tps or /cofh tps if you like
                |make sure to disable defaultCommand if you want your edits to have any effect
            """.trimMargin()
                    to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "forge tps",
                    help = "Print platform tps",
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
                    help = "Print platform world seed",
                    defaultCommand = true
            )),
            "uptime" to ("this is a reponse command, it uses the uptime function, time since the mod was first loaded"
                    to CustomCommand(
                    type = CommandType.RESPONSE,
                    response = "{uptime}",
                    help = "Print platform uptime",
                    defaultCommand = true
            )),
            "whoami" to ("this shows you some of the other response macros"
                    to CustomCommand(
                    type = CommandType.RESPONSE,
                    response = "name: `{user}` userid: `{userid}` platform: `{platform}` username: `{username}` uuid: `{uuid}`",
                    help = "Print debug user data",
                    timeout = 200,
                    defaultCommand = true
            )),
            "version" to ("are you out of date huh ?"
                    to CustomCommand(
                    type = CommandType.RESPONSE,
                    response = "{version}",
                    help = "are you out of date huh ?",
                    timeout = 200,
                    defaultCommand = true
            )),
            "exec" to ("this uses arguments in a passed-through command, you could restrict the arguments with a regex"
                    to CustomCommand(
                    type = CommandType.EXECUTE,
                    execute = "{args}",
                    argumentsRegex = ".*".toRegex(),
                    permLevel = 50.0,
                    help = "Execute any command as OP, be careful with this one",
                    execOp = true,
                    defaultCommand = true
            ))
    )

    val commands: CommandMap = hashMapOf()

    fun loadFile() {
        val jankson = Jankson
                .builder()
                .registerTypeAdapter { jsonObj ->
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
                .registerPrimitiveTypeAdapter {
                    it.toString().toRegex()
                }
                .build()

        jankson.marshaller.registerSerializer(Regex::class.java) { regex, _ ->
            JsonPrimitive(regex.pattern)
        }

        val jsonObject = try {
            jankson.load(configFile)
        } catch (e: SyntaxError) {
            logger.error("error parsing config: ${e.completeMessage}")
            JsonObject()
        } catch (e: FileNotFoundException) {
            configFile.createNewFile()
            JsonObject()
        }
        // clear commands
        commands.clear()
        jsonObject.forEach { key, element ->
            logger.trace("loading command '$key'")
            val command = jsonObject.get(CustomCommand::class.java, key)
            if (command != null)
                commands[key] = command
            else {
                logger.error("could not parse key: $key, value: '$element' as CustomCommand")
                logger.error("skipping $key")
            }
        }

        //apply defaults
        default.forEach { k, (comment, defCommand) ->
            val command = commands[k]
            if (command == null || command.defaultCommand == true) {
                commands[k] = defCommand
                jsonObject.getOrDefault(k, defCommand, comment)
            }
        }

        logger.debug("loaded jsonObj: $jsonObject")
        logger.debug("loaded commandMap: $commands")

        val defaultJsonObject = jankson.marshaller.serialize(CustomCommand.DEFAULT) as JsonObject
        val nonDefaultJsonObj = jsonObject.clone()
        jsonObject.forEach { key, element ->
            if (element is JsonObject) {
                nonDefaultJsonObj[key] = element.getDelta(defaultJsonObject)
            }
        }
        configFile.writeText(nonDefaultJsonObj.toJson(true, true))
    }
}