package game;


import base.datasets.UserDataSet;
import org.jetbrains.annotations.Nullable;
import messagesystem.Abonent;

public interface GameMechanics extends Abonent, Runnable {

    @Nullable GameSession getUserGameSession(UserDataSet user);

    boolean hasUserGameSession(UserDataSet user);

    GameUser getGameUser(UserDataSet user);

    void addUserForRandomGame(GameUser gameUser);

    void addUserForBotGame(GameUser gameUser);

    void addUserForFriendGame(GameUser gameUser, @Nullable Long gameSessionId);

    void removeUser(GameUser gameUser);

    @Override
    void run();
}
