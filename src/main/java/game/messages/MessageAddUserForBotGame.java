package game.messages;

import game.GameMechanics;
import game.GameSession;
import game.GameUser;
import messagesystem.Address;

public class MessageAddUserForBotGame extends MessageToMechanics {
    private final GameUser gameUser;

    public MessageAddUserForBotGame(Address from, Address to, GameUser user) {
        super(from, to);
        this.gameUser = user;
    }

    @Override
    protected void exec(GameMechanics gameMechanics) {
        gameMechanics.addUserForBotGame(this.gameUser);
    }
}
