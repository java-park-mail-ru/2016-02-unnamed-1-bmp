package game;


import base.datasets.UserDataSet;
import org.jetbrains.annotations.Nullable;

public interface GameMechanics {

    GameSession getUserGameSession(UserDataSet user);

    boolean hasUserGameSession(UserDataSet user);

    GameUser getGameUser(UserDataSet user);

    boolean addUserForRandomGame(GameUser gameUser);

    boolean addUserForBotGame(GameUser gameUser);

    boolean addUserForFriendGame(GameUser gameUser, @Nullable Long gameSessionId);

    boolean removeUser(GameUser gameUser);

    void run();
}
