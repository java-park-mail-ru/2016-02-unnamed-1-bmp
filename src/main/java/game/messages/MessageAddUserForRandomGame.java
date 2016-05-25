package game.messages;

import game.GameMechanics;
import game.GameUser;
import messagesystem.Address;

public class MessageAddUserForRandomGame extends MessageToMechanics {
    private final GameUser gameUser;

    public MessageAddUserForRandomGame(Address from, Address to, GameUser gameUser) {
        super(from, to);
        this.gameUser = gameUser;
    }

    @Override
    protected void exec(GameMechanics gameMechanics) {
        gameMechanics.addUserForRandomGame(this.gameUser);
    }
}
