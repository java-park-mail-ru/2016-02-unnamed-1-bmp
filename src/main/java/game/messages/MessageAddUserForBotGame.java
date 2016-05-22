package game.messages;

import game.GameMechanics;
import game.GameSession;
import game.GameUser;
import messagesystem.Address;

public class MessageAddUserForBotGame extends MessageToMechanics {
    private GameUser gameUser;
    private GameSession gameSession;

    public MessageAddUserForBotGame(Address from, Address to, GameUser user, GameSession gameSession) {
        super(from, to);
        this.gameUser = user;
        this.gameSession = gameSession;
    }

    @Override
    protected void exec(GameMechanics gameMechanics) {
        gameMechanics.addUserForBotGame(gameUser, gameSession);
    }
}
