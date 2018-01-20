package arcanitor.civilengineering.bridge;

import arcanitor.civilengineering.CivilEngineering;
import arcanitor.civilengineering.Config;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

public class MessageHandler implements Runnable {

    public static ConcurrentLinkedQueue<ApiMessage> xmitQueue = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<ApiMessage> rcvQueue = new ConcurrentLinkedQueue<>();

    public void run() {
        CivilEngineering.logger.info("Connecting to bridge server @ "+Config.connectURL);
        try {
            while(true) {
                transmitFromQueue();
                receiveToQueue();
                sleep(50);
            }
        } catch (Exception e) {

            if (e instanceof InterruptedException) {
                CivilEngineering.logger.info("Connection closed.");
            } else if (e instanceof IOException) {
                CivilEngineering.logger.error("Error connecting to bridge server!");
                CivilEngineering.logger.error(e.getMessage());
            }
        }

    }

    private void transmitFromQueue() throws IOException {
        ApiMessage nextMessage = xmitQueue.poll();
        while(nextMessage != null) {
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
            String json = nextMessage.encode();

            //send the message
            connection.setDoOutput(true);
            DataOutputStream post = new DataOutputStream(connection.getOutputStream());
            post.writeBytes(json);
            post.flush();
            post.close();

            if (connection.getResponseCode()!=200) {
                CivilEngineering.logger.error("Server returned "+connection.getResponseCode());
                break;
            }
        }

    }
    private void receiveToQueue() throws IOException {
        ApiMessage[] messages;

        //open a connection
        URL url = new URL(Config.connectURL + "/api/messages");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        //configure the connection
        con.setAllowUserInteraction(false);
        con.setInstanceFollowRedirects(true);
        if (Config.authToken != null) {
            con.setRequestProperty ("Authorization", "Bearer " + Config.authToken);
        }

        //read the messages
        BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder data =  new StringBuilder();
        String line;
        while((line = input.readLine( )) != null) {
            data.append(line);
        }

        //decode the messages
        Gson gson = new Gson();
        messages = gson.fromJson(data.toString(),ApiMessage[].class);

        //enqueue the messages
        if(messages.length>0) for (ApiMessage msg : messages) rcvQueue.add(msg);
    }
}
