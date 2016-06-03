package frontend;

import base.WebSocketService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import game.GameFieldShootResult;
import game.GameUser;
import main.Context;
import messagesystem.Address;
import messagesystem.MessageSystem;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class WebSocketServiceImpl implements WebSocketService {
    private static final Logger LOGGER = LogManager.getLogger(WebSocketServiceImpl.class);
    private static final long STEP_TIME = 200;
    private final ConcurrentMap<Long, GameWebSocket> sockets = new ConcurrentHashMap<>();
    private final Address address = new Address();
    private final MessageSystem messageSystem;

    public WebSocketServiceImpl(Context context) {
        messageSystem = (MessageSystem) context.get(MessageSystem.class);
    }

    @Override
    public void addSocket(@NotNull GameWebSocket socket) {
        final Long userId = socket.getUser().getId();
        final GameWebSocket previous = this.sockets.get(userId);
        if (previous != null && previous.isOpen()) {
            previous.close();
        }
        this.sockets.put(userId, socket);
    }

    @Override
    public void removeSocket(@NotNull GameWebSocket socket) {
        final Long userId = socket.getUser().getId();
        if (this.sockets.remove(userId, socket)) {
            if (socket.isOpen()) {
                socket.close();
            }
        }
    }

    @Override
    public void removeSocketByUser(@NotNull GameUser gameUser) {
        if (gameUser.getUser() != null) {
            final GameWebSocket socket = this.sockets.get(gameUser.getUser().getId());
            if (this.sockets.remove(gameUser.getUser().getId(), socket)) {
                socket.close();
            }
        }
    }

    @Override
    public boolean isOnline(@NotNull GameUser gameUser) {
        if (gameUser.getUser() != null) {
            final long userId = gameUser.getUser().getId();
            final GameWebSocket socket = this.sockets.get(userId);

            return socket != null && socket.isOpen();
        }

        return gameUser.isBot();
    }

    @Override
    public void notify(@NotNull GameUser gameUser, GameWebSocketMessage message) {
        if (gameUser.getUser() != null) {
            final Long userId = gameUser.getUser().getId();
            final GameWebSocket gameWebSocket = this.sockets.get(userId);
            if (gameWebSocket != null) {
                gameWebSocket.send(message);
            }
        }
    }

    @Override
    public void notifyInitGame(@NotNull GameUser gameUser, boolean ok,
                               @Nullable Long gameSessionId) {
        final GameWebSocketMessage notifyMessage =
                new GameWebSocketMessage(GameWebSocketMessage.MessageType.GAME_INIT);
        notifyMessage.setOk(ok);
        if (gameSessionId != null) {
            notifyMessage.setId(gameSessionId);
        }
        this.notify(gameUser, notifyMessage);
    }

    @Override
    public void notifyError(@NotNull GameUser gameUser, String error) {
        final GameWebSocketMessage notifyMessage =
                new GameWebSocketMessage(GameWebSocketMessage.MessageType.ERROR, error);
        this.notify(gameUser, notifyMessage);
    }

    @Override
    public void notifyTurn(@NotNull GameUser gameUser, boolean isHis) {
        final GameWebSocketMessage notifyMessage =
                new GameWebSocketMessage(GameWebSocketMessage.MessageType.GAME_TURN);
        notifyMessage.setOk(isHis);
        this.notify(gameUser, notifyMessage);
    }

    @Override
    public void notifyStartGame(@NotNull GameUser gameUser, @NotNull GameUser opponent) {
        final GameWebSocketMessage notifyMessage =
                new GameWebSocketMessage(GameWebSocketMessage.MessageType.GAME_START);
        notifyMessage.setOpponentName(opponent.getName());
        this.notify(gameUser, notifyMessage);
    }

    @Override
    public void notifyShootResult(@NotNull GameUser gameUser, @NotNull GameFieldShootResult result,
                                  boolean isMine) {

        final GameWebSocketMessage shootMessage =
                new GameWebSocketMessage(GameWebSocketMessage.MessageType.SHOOT_RESULT);
        shootMessage.setOk(isMine);
        shootMessage.setX(result.getX());
        shootMessage.setY(result.getY());
        shootMessage.setStatus(result.getState().toString());

        if (result.isKilled() && result.getShip() != null) {
            shootMessage.setStartX(result.getShip().getX());
            shootMessage.setStartY(result.getShip().getY());
            shootMessage.setLength(result.getShip().getLength());
            shootMessage.setVertical(result.getShip().isVertical());
        }
        this.notify(gameUser, shootMessage);
    }

    @Override
    public void notifyGameOver(@NotNull GameUser gameUser, boolean win) {
        final GameWebSocketMessage notifyMessage =
                new GameWebSocketMessage(GameWebSocketMessage.MessageType.GAME_OVER);
        notifyMessage.setOk(win);
        notifyMessage.setScore(gameUser.getScore());
        this.notify(gameUser, notifyMessage);
    }

    @Override
    public void notifyOpponentOnline(@NotNull GameUser gameUser, @NotNull GameUser opponent) {
        final GameWebSocketMessage notifyMessage =
                new GameWebSocketMessage(GameWebSocketMessage.MessageType.OPPONENT_ONLINE);
        notifyMessage.setOk(this.isOnline(opponent));
        this.notify(gameUser, notifyMessage);
    }

    @Override
    public void notifyTooLong(@NotNull GameUser gameUser) {
        final GameWebSocketMessage notifyMessage =
                new GameWebSocketMessage(GameWebSocketMessage.MessageType.GAME_TOO_LONG);
        this.notify(gameUser, notifyMessage);
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            messageSystem.execForAbonent(this);
            try {
                Thread.sleep(STEP_TIME);
            } catch (InterruptedException e) {
                LOGGER.error("Thread interrupted", e);
            }
        }
    }
}
