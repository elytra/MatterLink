package arcanitor.civilengineering;

import arcanitor.civilengineering.eventhandlers.FMLEventHandler;
import arcanitor.civilengineering.bridge.MessageHandler;
import arcanitor.civilengineering.server.ServerChatWriter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
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
    public static Thread networkThread = new Thread(new MessageHandler());

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
        FMLEventHandler.postInit(event);
        MinecraftForge.EVENT_BUS.register(ServerChatWriter.class);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event){
        logger.info("Building chat bridge");
        FMLEventHandler.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        logger.info("Demolishing chat bridge.");
        FMLEventHandler.serverStopping(event);
    }
}
