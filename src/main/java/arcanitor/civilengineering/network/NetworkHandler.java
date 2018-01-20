package arcanitor.civilengineering.network;

import arcanitor.civilengineering.CivilEngineering;

public class NetworkHandler implements Runnable {
    public void run() {
        CivilEngineering.logger.info("Network Thread");
    }
}
