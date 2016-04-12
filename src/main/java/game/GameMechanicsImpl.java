package game;

import base.GameMechanics;
import base.WebSocketService;
import utils.TimeHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class GameMechanicsImpl implements GameMechanics {
    private static final int STEP_TIME = 100;
    private WebSocketService webSocketService;
    private String waiter;

    private Set<GameSession> allSessions = new HashSet<>();
    private Map<String, GameSession> nameToGame = new HashMap<>();

    public GameMechanicsImpl(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }


    @Override
    public void addUser(String user) {
        if (waiter != null) {
            startGame(user);
            waiter = null;
        } else {
            waiter = user;
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

    }

    private void startGame(String first) {
        String second = waiter;
        GameSession gameSession = new GameSession(first, second);
        allSessions.add(gameSession);
        nameToGame.put(first, gameSession);
        nameToGame.put(second, gameSession);

        webSocketService.notifyStartGame(gameSession.getSelf(first));
        webSocketService.notifyStartGame(gameSession.getSelf(second));
    }
}
