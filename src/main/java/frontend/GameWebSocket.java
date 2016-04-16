package frontend;


import base.*;
import com.google.gson.*;
import dbservice.DatabaseException;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final int INDLE_SHIPS_NUM = 20;
    private String myName;
    private Long currentUserId;

    private Session session;
    private WebSocketService webSocketService;
    private GameMechanics gameMechanics;
    private UserService userService;

    private static final Logger LOGGER = LogManager.getLogger(GameWebSocket.class);


    GameWebSocket(String name, Context context, Long userId) {
        this.myName = name;
        this.currentUserId = userId;

        this.webSocketService = (WebSocketService) context.get(WebSocketService.class);
        this.gameMechanics = (GameMechanics) context.get(GameMechanics.class);
        this.userService = (UserService) context.get(UserService.class);
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
            LOGGER.error(e.getMessage());
        }
    }


    public void finishGame(boolean win) {
        try {
            if(win && currentUserId != -1){
                userService.incrementUserScore(currentUserId);
            }
            final JsonObject jsonStart = new JsonObject();
            jsonStart.add("action", new JsonPrimitive("gameOver"));
            final JsonObject body = new JsonObject();
            body.add("win", new JsonPrimitive(win));
            jsonStart.add("body", body);
            session.getRemote().sendString(jsonStart.toString());
        } catch (IOException | DatabaseException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void finishGameEnemyleft() {
        try {
            final JsonObject jsonStart = new JsonObject();
            jsonStart.add("action", new JsonPrimitive("enemyLeft"));
            session.getRemote().sendString(jsonStart.toString());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void shootAction(JsonObject shootResponce) {
        try {
            final JsonObject jsonShoot = new JsonObject();
            jsonShoot.add("action", new JsonPrimitive("shoot"));
            jsonShoot.add("body", shootResponce);
            session.getRemote().sendString(jsonShoot.toString());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void waitAction(JsonObject shootResponce) {
        try {
            final JsonObject jsonShoot = new JsonObject();
            jsonShoot.add("action", new JsonPrimitive("wait"));
            jsonShoot.add("body", shootResponce);
            session.getRemote().sendString(jsonShoot.toString());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @OnWebSocketMessage
    public void onMessage(String data) {
        try {
            final JsonElement jsonElement = new JsonParser().parse(data);
            final String action = jsonElement.getAsJsonObject().getAsJsonPrimitive("action").getAsString();
            if(action == null){
                throw new JsonSyntaxException("Can't find out action in JSON");
            }
            switch (action){
                case "set_ships":
                    final Map<String, String> userBoats = parseIncomingShips(jsonElement);
                    if(userBoats.size() != INDLE_SHIPS_NUM) {
                        final JsonObject error = new JsonObject();
                        error.add("error", new JsonPrimitive("Not enough ships"));
                        return;
                    }
                    gameMechanics.addUser(myName, userBoats);
                    break;
                case "shoot":
                    final JsonObject subJson = jsonElement.getAsJsonObject().getAsJsonObject("body");
                    final String coordiantes = subJson.getAsJsonArray("coordinates").toString();
                    gameMechanics.shoot(myName, coordiantes);
                    break;
                default:
                    throw new JsonSyntaxException("Unknown action");
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error(e.getMessage());
            final JsonObject error = new JsonObject();
            error.add("error", new JsonPrimitive(e.getMessage()));
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage());
            final JsonObject error = new JsonObject();
            error.add("error", new JsonPrimitive("Unexpected error"));
        }
    }


    @OnWebSocketConnect
    public void onOpen(Session ses) {
        this.session = ses;
        webSocketService.addUser(this);
    }


    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        webSocketService.removeUser(this);
        gameMechanics.removeUser(myName);
        LOGGER.info("Closing socket status: {} reason: {}", statusCode, reason);
    }

    public Session getSession() {
        return session;
    }

    private Map<String, String>  parseIncomingShips(JsonElement ships) throws JsonSyntaxException {
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
