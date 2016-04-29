package game;


import base.GameUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GameSession {
    private final long startTime;
    private final GameUser first;
    private final GameUser second;

    private final Map<String, GameUser> users = new HashMap<>();

    public GameSession(String first, Map<String, String>  firstBoats,
                       String second, Map<String, String> secondBoats) {
        startTime = new Date().getTime();
        final GameUser firstGameUser = new GameUser(first, firstBoats);
        firstGameUser.setEnemyName(second);

        final GameUser secondGameUser = new GameUser(second, secondBoats);
        secondGameUser.setEnemyName(first);

        users.put(first, firstGameUser);
        users.put(second, secondGameUser);

        this.first = firstGameUser;
        this.second = secondGameUser;
    }

    public GameUser getEnemy(String user) {
        final String enemyName = users.get(user).getEnemyName();
        return users.get(enemyName);
    }

    public long getSessionTime(){
        return new Date().getTime() - startTime;
    }

    public GameUser getFirst() {
        return first;
    }

    public GameUser getSecond() {
        return second;
    }

    public GameUser getSelf(String user) {
        return users.get(user);
    }
}
