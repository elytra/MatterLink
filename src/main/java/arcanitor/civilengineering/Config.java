package arcanitor.civilengineering;

import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

public class Config {
    private static final String CATEGORY_RELAY_OPTIONS = "relay_options";
    private static final String CATEGORY_CONNECTION = "connection";

    public static boolean relayDeathEvents = false;
    public static boolean relayAdvancements = false; //unused for now

    public static String connectURL = "localhost";

    public static void readConfig() {
        Configuration config = CivilEngineering.config;
        try {
            config.load();
            initConfig(config);
        } catch (Exception expt) {
            CivilEngineering.logger.log(Level.ERROR,"Could not read config file!", expt);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    private static void initConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_RELAY_OPTIONS,"Relay options");
        cfg.addCustomCategoryComment(CATEGORY_CONNECTION,"Connection settings");
        relayDeathEvents = cfg.getBoolean(
                "relayDeathEvents",
                CATEGORY_RELAY_OPTIONS,
                false,
                "Set to true to send death messages over the chat relay."
        );
        relayAdvancements = cfg.getBoolean(
                "relayAdvancements",
                CATEGORY_RELAY_OPTIONS,
                false,
                "This option does nothing as advancement relays are not implemented."
        );
        connectURL = cfg.getString(
                "connectURL",
                CATEGORY_CONNECTION,
                "localhost",
                "The URL or IP address of the bridge server"
        );

    }

}