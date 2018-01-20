package arcanitor.civilengineering.network;

import arcanitor.civilengineering.CivilEngineering;

public class NetworkHandler implements Runnable {
    public void run() {
        CivilEngineering.logger.info("Network Thread");
    }

    public static void Main(String[] args) {
        (new Thread(new NetworkHandler())).start();
    }
}
