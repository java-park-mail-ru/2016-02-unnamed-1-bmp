package game;


import base.datasets.UserDataSet;
import com.sun.istack.internal.Nullable;
import messagesystem.Abonent;

public interface GameMechanics extends Abonent, Runnable {

    GameSession getUserGameSession(UserDataSet user);

    boolean hasUserGameSession(UserDataSet user);

    GameUser getGameUser(UserDataSet user);

    boolean addUserForRandomGame(GameUser gameUser);

    boolean addUserForBotGame(GameUser gameUser);

    boolean addUserForFriendGame(GameUser gameUser, @Nullable Long gameSessionId);

    boolean removeUser(GameUser gameUser);

    void run();
}
