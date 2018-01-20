package arcanitor.civilengineering.bridge;

import arcanitor.civilengineering.CivilEngineering;
import arcanitor.civilengineering.Config;
import com.google.gson.Gson;

public class ApiMessage {
    private String text = "";
    private String channel = "";
    private String username = "";
    private String userid = "";
    private String avatar = "";
    private String gateway = "";
    private String event = "";

    public ApiMessage(String user, String msg) {
        this.username = user;
        this.text = msg;
        this.gateway = Config.gateway;

    }
    public ApiMessage(String user, String msg, String event) {
        this.username = user;
        this.text = msg;
        this.event = event;
    }

    public static ApiMessage decode(String json) {
        Gson gson = new Gson();
        ApiMessage msg = gson.fromJson(json, ApiMessage.class);
        return msg;

    }

    public String encode() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getUsername() {
        return this.username;
    }

    public String getMessage() {
        return this.text;
    }

    public String getEvent() {
        return this.event;
    }



}
