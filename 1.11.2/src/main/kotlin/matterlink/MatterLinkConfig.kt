package matterlink

import matterlink.config.BaseConfig
import matterlink.config.cfg
import net.minecraftforge.common.config.Configuration
import java.io.File

class MatterLinkConfig(file: File) : BaseConfig() {
    init {
        logger.info("Reading bridge blueprints... from {}", file)
        val config = Configuration(file)

        load(
                getBoolean = config::getBoolean,
                getString = config::getString,
                getStringValidated = config::getString,
                getStringList = config::getStringList,
                addCustomCategoryComment = config::addCustomCategoryComment
        )

        if (config.hasChanged()) config.save()

        cfg = this
    }
}