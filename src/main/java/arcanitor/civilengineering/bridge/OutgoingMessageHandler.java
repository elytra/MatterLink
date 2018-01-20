package arcanitor.civilengineering.bridge;

import arcanitor.civilengineering.CivilEngineering;
import arcanitor.civilengineering.Config;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

public class OutgoingMessageHandler implements Runnable {
    public static ConcurrentLinkedQueue<ApiMessage> queue = new ConcurrentLinkedQueue();

    public void run() {
        CivilEngineering.logger.info("Sending network thread starting");
        try {
            while(true) {
                ApiMessage nextMessage = queue.poll();
                if (nextMessage!=null) {
                    int response = postMessage(nextMessage);
                    if (response != 200) {
                        CivilEngineering.logger.error("Server returned error "+response);
                        break;
                    }
                }
                sleep(50);
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                CivilEngineering.logger.info("Sending connection closed.");
            } else if (e instanceof IOException) {
                CivilEngineering.incomingMessageThread.interrupt();
                CivilEngineering.logger.error("Error connecting to bridge server!");
                CivilEngineering.logger.error(e.getMessage());
            }

        }
    }

    public int postMessage(ApiMessage message) throws IOException {

        //open a connection
        URL url = new URL(Config.connectURL + "/api/message");
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection connection = (HttpURLConnection)urlConnection;

        //configure the connection
        connection.setAllowUserInteraction(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestMethod("POST");
        if (Config.authToken != null) {
            connection.setRequestProperty ("Authorization", "Bearer " + Config.authToken);
        }

        //encode the ApiMessage for sending
        String json = message.encode();

        //send the message
        connection.setDoOutput(true);
        DataOutputStream post = new DataOutputStream(connection.getOutputStream());
        post.writeBytes(json);
        post.flush();
        post.close();

        int response = connection.getResponseCode();

        connection.disconnect();

        return response;
    }
}
