package frontend;


import base.AccountService;
import base.GameMechanics;
import base.GameUser;
import base.WebSocketService;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import main.Context;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;


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

    @OnWebSocketMessage
    public void onMessage(String data) {
    }


    @OnWebSocketConnect
    public void onOpen(Session session) {
        this.session = session;
        webSocketService.addUser(this);
        gameMechanics.addUser(myName);
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

}
