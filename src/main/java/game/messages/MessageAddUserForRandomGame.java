package game.messages;

import game.GameMechanics;
import game.GameSession;
import game.GameUser;
import messagesystem.Address;

public class MessageAddUserForRandomGame extends MessageToMechanics {
    private final GameUser gameUser;
    private final GameSession gameSession;

    public MessageAddUserForRandomGame(Address from, Address to, GameUser gameUser, GameSession gameSession) {
        super(from, to);
        this.gameUser = gameUser;
        this.gameSession = gameSession;
    }

    @Override
    protected void exec(GameMechanics gameMechanics) {
        gameMechanics.addUserForRandomGame(gameUser, gameSession);
    }
}
