package base;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import frontend.GameWebSocket;
import frontend.GameWebSocketMessage;
import game.GameFieldShootResult;
import game.GameUser;
import messagesystem.Abonent;

public interface WebSocketService extends Abonent, Runnable {

    void addSocket(@NotNull GameWebSocket socket);

    void removeSocket(@NotNull GameWebSocket socket);

    void removeSocketByUser(@NotNull GameUser gameUser);

    void notify(@NotNull GameUser gameUser, GameWebSocketMessage message);

    void notifyInitGame(@NotNull GameUser gameUser, boolean ok, @Nullable Long gameSessionId);

    void notifyStartGame(@NotNull GameUser gameUser, @NotNull GameUser opponent);

    void notifyError(@NotNull GameUser gameUser, String error);

    void notifyTurn(@NotNull GameUser gameUser, boolean isHis);

    void notifyShootResult(@NotNull GameUser gameUser, @NotNull GameFieldShootResult result, boolean isMine);

    void notifyGameOver(@NotNull GameUser gameUser, boolean win);

    void notifyOpponentOnline(@NotNull GameUser gameUser, @NotNull GameUser opponent);

    void notifyTooLong(@NotNull GameUser gameUser);

    boolean isOnline(@NotNull GameUser gameUser);
}

