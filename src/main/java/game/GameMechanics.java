package game;


import base.datasets.UserDataSet;
import com.sun.istack.internal.Nullable;
import messagesystem.Abonent;

public interface GameMechanics extends Abonent, Runnable {

    GameSession getUserGameSession(UserDataSet user);

    boolean hasUserGameSession(UserDataSet user);

    GameUser getGameUser(UserDataSet user);

    void addUserForRandomGame(GameUser gameUser, GameSession gameSession);

    void addUserForBotGame(GameUser gameUser, GameSession gameSession);

    void addUserForFriendGame(GameUser gameUser, @Nullable Long gameSessionId);

    void removeUser(GameUser gameUser);

    @Override
    void run();
}
