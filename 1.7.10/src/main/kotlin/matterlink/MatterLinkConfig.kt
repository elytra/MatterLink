package matterlink

import matterlink.config.BaseConfig
import net.minecraftforge.common.config.Configuration
import java.io.File

class MatterLinkConfig(val file: File) : BaseConfig() {
    init {
        logger.info("Reading bridge blueprints... from {}", file)
        val config = Configuration(file)

        load(
                getBoolean = config::getBoolean,
                getString = config::getString,
                getStringValidated = config::getString,
                getStringValidValues = config::getString,
                getStringList = config::getStringList,
                addCustomCategoryComment = config::addCustomCategoryComment
        )

        if (config.hasChanged()) config.save()
    }

    override fun load() = MatterLinkConfig(file)
}