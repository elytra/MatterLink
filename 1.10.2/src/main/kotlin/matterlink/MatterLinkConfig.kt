package matterlink

import matterlink.config.BaseConfig
import matterlink.config.cfg
import net.minecraftforge.common.config.Configuration
import java.io.File

class MatterLinkConfig(val baseCfgDir: File) : BaseConfig(baseCfgDir) {
    init {
        logger.info("Reading bridge blueprints... from {}", cfgDirectory)
        val config = Configuration(mainCfgFile)

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

    override fun load() = MatterLinkConfig(baseCfgDir)
}