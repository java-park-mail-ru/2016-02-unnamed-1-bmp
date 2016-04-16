package base;

import com.google.gson.JsonObject;
import frontend.GameWebSocket;

public interface WebSocketService {

    void addUser(GameWebSocket user);

    void notifyStartGame(GameUser user);

    void notifyAct(GameUser user, JsonObject shoot);

    void notifyWait(GameUser user, JsonObject shoot);

    void notifyGameOver(GameUser user, boolean win);
}

