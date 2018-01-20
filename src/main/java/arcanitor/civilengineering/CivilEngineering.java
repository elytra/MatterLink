package arcanitor.civilengineering;

import arcanitor.civilengineering.eventhandlers.FMLEventHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = CivilEngineering.MODID,
        name = CivilEngineering.NAME,
        version = CivilEngineering.VERSION,
        serverSideOnly = true,
        useMetadata = true,
        acceptableRemoteVersions = "*"
)
public class CivilEngineering {
    public static final String MODID = "civilengineering";
    public static final String NAME = "Civil Engineering";
    public static final String VERSION = "0.0.1";

    @Mod.Instance(value = CivilEngineering.MODID)
    public static CivilEngineering instance;

    public static Logger logger;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        FMLEventHandler.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Bridge building init.");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        logger.info("Bridge building post-init.");

        FMLEventHandler.postInit(event);
    }

}