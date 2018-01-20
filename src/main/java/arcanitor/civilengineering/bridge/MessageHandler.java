package arcanitor.civilengineering.bridge;

import arcanitor.civilengineering.CivilEngineering;
import arcanitor.civilengineering.Config;
import arcanitor.civilengineering.server.ServerChatWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MessageHandler implements Runnable {
    private HttpURLConnection connection = null;

    public void run() {
        CivilEngineering.logger.info("Network Thread starting.");
        try {
            connect(Config.connectURL,Config.authToken);
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = input.readLine( )) != null) {
                CivilEngineering.logger.debug(line);
                ServerChatWriter.messages.add(ApiMessage.decode(line));
                Thread.sleep(1);
            }

        } catch (Exception e) {

            if (e instanceof InterruptedException) {
                connection.disconnect();    //close the connection
                CivilEngineering.logger.info("Bridge demolished!");
            } else if (e instanceof IOException) {
                CivilEngineering.logger.error("Bridge construction failed!");
                CivilEngineering.logger.error(e.getMessage());
            }
        }

    }

    private void connect(String serverURL, String token) throws IOException {
        URL url = new URL(serverURL+"/api/stream");   //parse the server URL
        URLConnection urlConnection = url.openConnection();

        //set connection properties
        connection = (HttpURLConnection)urlConnection;
        connection.setAllowUserInteraction(false);
        connection.setInstanceFollowRedirects(true);

        if (token != null) {
            connection.setRequestProperty ("Authorization", "Bearer " + token);
        }
    }
}
