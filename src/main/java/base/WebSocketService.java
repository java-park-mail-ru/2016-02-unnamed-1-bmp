package base;

import frontend.GameWebSocket;

public interface WebSocketService {

    void addUser(GameWebSocket user);

    void removeUser(GameUser user);

    void notifyStartGame(GameUser user);

    void notifyWait(GameUser user);

    void notifyAct(GameUser user);

    void notifyGameOver(GameUser user, boolean win);
}

