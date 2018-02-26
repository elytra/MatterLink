package matterlink.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import io.github.config4k.extract
import io.github.config4k.toConfig
import matterlink.instance
import matterlink.stackTraceString
import java.io.File


object PermissionConfig {
    private val configFile: File = cfg.cfgDirectory.resolve("permissions.hocon")

    private val default = mapOf(
            "irc.esper" to mapOf(
                    0 to listOf(
                            "~nikky@nikky.moe",
                            "user@example.com"
                    )
            )
    )

    private val defaultString = """
        |permissions {
        |    "irc.esper" {
        |        0: [
        |        "~nikky@nikky.moe"
        |        "user@example.com"
        |        ]
        |    }
        |    "discord.game" {
        |        0: [
        |        112228624366575616
        |        ]
        |    }
        |}
        """.trimMargin()

    var perms: Map<String, Map<Int, List<String>>> = default

    val options = ConfigRenderOptions
            .defaults()
            .setJson(false)
            .setOriginComments(false)

    fun loadPermFile(): Boolean {
        if (!configFile.exists() || configFile.readText().isBlank()) {
            configFile.createNewFile()
//            val config: Config = default.toConfig("permissions")
//            configFile.writeText(config.root().render(options))
            //TODO: temporary fix see: https://github.com/config4k/config4k/issues/43
            configFile.writeText(defaultString)
            return true
        }

        var success = true
        perms = try {
            val config = ConfigFactory.parseFile(configFile)
            val permConfig = config.getConfig("permissions")

            val serverKeys = config.getObject("permissions")
                    .unwrapped()
                    .keys
            println(permConfig.root().render())
            println(serverKeys)
            perms = serverKeys.associate { serverKey ->
//                val serverConfig = permConfig.getConfig(serverKey)
//                val levels = permConfig.getObject(serverKey)
//                        .unwrapped()
//                        .keys
                val serverConfig = permConfig.getConfig("\""+serverKey+"\"")
                val levels = permConfig.getObject("\""+serverKey+"\"")
                        .unwrapped()
                        .keys
                serverKey to levels.associate { it.toInt() to serverConfig.extract<List<String>>(it)  }
            }
            perms

        } catch (e: ConfigException) {
            instance.fatal(e.stackTraceString)
            instance.fatal("failed to parse $configFile using last good values as fallback")
            success = false
            perms
        }
        return success
    }
}