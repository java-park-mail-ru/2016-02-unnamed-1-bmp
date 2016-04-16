package game;

import base.GameMechanics;
import base.GameUser;
import base.WebSocketService;
import com.google.gson.JsonObject;
import com.sun.istack.internal.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.TimeHelper;

import java.util.*;
import java.util.Map;
import java.util.Set;


public class GameMechanicsImpl implements GameMechanics{
    private static final int STEP_TIME = 100;
    private static final int GAME_TIME = 10 * 60 * 1000;
    private static final Logger LOGGER = LogManager.getLogger(GameMechanicsImpl.class);

    private WebSocketService webSocketService;
    private String waiter;
    private Map<String, String> waiterBoats;

    private Set<GameSession> allSessions = new HashSet<>();
    private Map<String, GameSession> nameToGame = new HashMap<>();

    public GameMechanicsImpl(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }


    @Override
    public void addUser(String user, Map<String, String> userBoats) {
        if (waiter != null) {
            startGame(user, userBoats);
            waiter = null;
            waiterBoats = null;
        } else {
            waiter = user;
            waiterBoats = new HashMap<>(userBoats);
        }
    }

    @Override
    public void removeUser(@NotNull String user) {
        if(waiter != null && waiter.equals(user)){
            waiter = null;
            waiterBoats = null;
        }
        final GameSession myGameSession = nameToGame.get(user);
        if(myGameSession != null) {
            final GameUser enemyUser = myGameSession.getEnemy(user);
            webSocketService.notifyEnemyLeft(enemyUser);
        }
    }

    @Override
    public void run() {
        while (true) {
            gmStep();
            TimeHelper.sleep(STEP_TIME);
        }
    }

    private void gmStep() {
        for (GameSession session : allSessions) {
            if (session.getSessionTime() > GAME_TIME) {
                webSocketService.notifyGameOver(session.getFirst(), true);
                webSocketService.notifyGameOver(session.getSecond(), true);

                nameToGame.values().removeAll(Collections.singleton(session));
                allSessions.remove(session);
            }
        }
    }

    @Override
    public void shoot(String userName, String coordinates){
        final GameSession myGameSession = nameToGame.get(userName);
        final GameUser myUser = myGameSession.getSelf(userName);
        final GameUser enemyUser = myGameSession.getEnemy(userName);
        final JsonObject shootResponce = enemyUser.shootMyShip(coordinates);

        final String status = shootResponce.get("status").getAsString();
        if(status.equals("lost")){
            webSocketService.notifyGameOver(myUser, true);
            webSocketService.notifyGameOver(enemyUser, false);
            LOGGER.info("Finished game for players. Winner:{} Loser: {}", myUser, enemyUser);
            return;
        }
        webSocketService.notifyWait(myUser, shootResponce);
        webSocketService.notifyAct(enemyUser, shootResponce);
    }

    private void startGame(String first, Map<String, String> firstBoats) {

        final String second = waiter;
        final Map<String, String> secondBoats = new HashMap<>(waiterBoats);


        final GameSession gameSession = new GameSession(first, firstBoats, second, secondBoats);

        allSessions.add(gameSession);
        nameToGame.put(first, gameSession);
        nameToGame.put(second, gameSession);

        webSocketService.notifyStartGame(gameSession.getSelf(first));
        webSocketService.notifyStartGame(gameSession.getSelf(second));

        LOGGER.info("Started game for players {} vs. {}", first, second);
    }
}
