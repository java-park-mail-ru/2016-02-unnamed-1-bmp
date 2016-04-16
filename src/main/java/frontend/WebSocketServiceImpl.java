package frontend;

import base.GameUser;
import base.WebSocketService;
import com.google.gson.JsonObject;
import com.sun.istack.internal.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class WebSocketServiceImpl implements WebSocketService {
    private ConcurrentMap<String, GameWebSocket> userSockets = new ConcurrentHashMap<>();

    @Override
    public void addUser(@NotNull GameWebSocket user) {
        userSockets.put(user.getMyName(), user);
    }


    @Override
    public void removeUser(@NotNull GameWebSocket user) {
        userSockets.remove(user.getMyName());
    }


    @Override
    public void notifyStartGame(GameUser user) {
        final GameWebSocket gameWebSocket = userSockets.get(user.getMyName());
        if (gameWebSocket != null) {
            gameWebSocket.startGame(user);
        }
    }


    @Override
    public void notifyAct(GameUser user, JsonObject act) {
        final GameWebSocket gameWebSocket = userSockets.get(user.getMyName());
        if (gameWebSocket != null) {
            gameWebSocket.shootAction(act);
        }
    }

    @Override
    public void notifyWait(GameUser user, JsonObject act) {
        final GameWebSocket gameWebSocket = userSockets.get(user.getMyName());
        if (gameWebSocket != null) {
            gameWebSocket.waitAction(act);
        }
    }

    @Override
    public void notifyGameOver(GameUser user, boolean win) {
        userSockets.get(user.getMyName()).finishGame(win);
        userSockets.remove(user.getMyName());
    }

    @Override
    public void notifyEnemyLeft(GameUser user) {
        final GameWebSocket gameWebSocket = userSockets.get(user.getMyName());
        if (gameWebSocket != null) {
            gameWebSocket.finishGameEnemyleft();
        }
    }

}
