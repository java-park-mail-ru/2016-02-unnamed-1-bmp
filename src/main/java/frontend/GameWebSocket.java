package frontend;


import base.AccountService;
import base.GameMechanics;
import base.GameUser;
import base.WebSocketService;
import com.google.gson.*;
import main.Context;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebSocket
public class GameWebSocket {
    private String myName;
    private Long currentUserId;
    private Session session;
    private AccountService accountService;
    private WebSocketService webSocketService;
    private GameMechanics gameMechanics;


    GameWebSocket(String name, Context context, String userSessionId) {
        this.myName = name;
        this.accountService = (AccountService) context.get(AccountService.class);
//        this.currentUserId = accountService.getUserIdBySesssion(userSessionId);
        this.webSocketService = (WebSocketService) context.get(WebSocketService.class);
        this.gameMechanics = (GameMechanics) context.get(GameMechanics.class);
    }

    public String getMyName() {
        return myName;
    }

    public void startGame(GameUser user) {
        try {
            final JsonObject jsonStart = new JsonObject();
            jsonStart.add("action", new JsonPrimitive("start"));
            final JsonObject body = new JsonObject();
            body.add("myName", new JsonPrimitive(user.getMyName()));
            body.add("enemyName", new JsonPrimitive(user.getEnemyName()));
            jsonStart.add("body", body);
            session.getRemote().sendString(jsonStart.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void finishGame(GameUser user, boolean win) {
        try {
            final JsonObject jsonStart = new JsonObject();
            jsonStart.add("action", new JsonPrimitive("gameOver"));
            final JsonObject body = new JsonObject();
            body.add("win", new JsonPrimitive(win));
            jsonStart.add("body", body);
            session.getRemote().sendString(jsonStart.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void shootAction(JsonObject shootResponce) {
        try {
            final JsonObject jsonShoot = new JsonObject();
            jsonShoot.add("action", new JsonPrimitive("shoot"));
            jsonShoot.add("body", shootResponce);
            session.getRemote().sendString(jsonShoot.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waitAction(JsonObject shootResponce) {
        try {
            final JsonObject jsonShoot = new JsonObject();
            jsonShoot.add("action", new JsonPrimitive("wait"));
            jsonShoot.add("body", shootResponce);
            session.getRemote().sendString(jsonShoot.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        final JsonElement jsonElement = new JsonParser().parse(data);
        final JsonPrimitive jsonObject = jsonElement.getAsJsonObject().getAsJsonPrimitive("action");
        if (jsonObject.equals(new JsonPrimitive("set_ships"))) {
            final Map<String, String> userBoats = parseIncomingShips(jsonElement);
            gameMechanics.addUser(myName, userBoats);
        } else if (jsonObject.equals(new JsonPrimitive("shoot"))){
            final JsonObject subJson = jsonElement.getAsJsonObject().getAsJsonObject("body");
            final String coordiantes = subJson.getAsJsonArray("coordinates").toString();
            gameMechanics.shoot(myName, coordiantes);
        }

    }


    @OnWebSocketConnect
    public void onOpen(Session session) {
        this.session = session;
        webSocketService.addUser(this);
    }


    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {

    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    private Map<String, String>  parseIncomingShips(JsonElement ships) {
        final Map<String, String> userBoats = new HashMap<>();
        final JsonObject subJson = ships.getAsJsonObject().getAsJsonObject("body");
        for (int i = 0; i < 4; ++i) {
            final String fourPlace = subJson.getAsJsonArray("four-decked").get(i).toString();
            userBoats.put(fourPlace, "four-decked");
            final String onePlace = subJson.getAsJsonArray("one-decked").get(i).toString();
            userBoats.put(onePlace, "one-decked");
        }
        for (int i = 0; i < 2; ++i) {
            final JsonArray jsonArray = subJson.getAsJsonArray("three-decked").get(i).getAsJsonArray();
            final String shipName = "three-decked" + jsonArray.get(0).toString();
            for (int j = 0; j < 3; ++j) {
                final String threePlace = jsonArray.get(j).toString();
                ;
                userBoats.put(threePlace, shipName);
            }
        }
        for (int i = 0; i < 3; ++i) {
            final JsonArray jsonArray = subJson.getAsJsonArray("two-decked").get(i).getAsJsonArray();
            final String shipName = "two-decked" + jsonArray.get(0).toString();
            for (int j = 0; j < 2; ++j) {
                final String threePlace = jsonArray.get(j).toString();

                userBoats.put(threePlace, shipName);
            }
        }
        return userBoats;
    }
}
