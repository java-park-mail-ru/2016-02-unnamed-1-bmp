package game;

import base.GameMechanics;
import base.GameUser;
import base.WebSocketService;
import com.google.gson.JsonObject;
import utils.TimeHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class GameMechanicsImpl implements GameMechanics{
    private static final int STEP_TIME = 100;
    private WebSocketService webSocketService;
    private String waiter;
    private Map<String, String> waiterBoats;

    private Set<GameSession> allSessions = new HashSet<GameSession>();
    private Map<String, GameSession> nameToGame = new HashMap<String, GameSession>();

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
    public void run() {
        while (true) {
            gmStep();
            TimeHelper.sleep(STEP_TIME);
        }
    }

    private void gmStep() {
//        for (GameSession session : allSessions) {
//            if (session.getSessionTime() > gameTime) {
//                boolean firstWin = session.isFirstWin();
//                webSocketService.notifyGameOver(session.getFirst(), firstWin);
//                webSocketService.notifyGameOver(session.getSecond(), !firstWin);
//            }
//        }
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
    }
}
