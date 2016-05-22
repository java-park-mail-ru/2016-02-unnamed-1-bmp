package game.messages;

import game.GameMechanics;
import game.GameUser;
import messagesystem.Address;

public class MessageRemoveUser extends MessageToMechanics {
    private final GameUser gameUser;

    public MessageRemoveUser(Address from, Address to, GameUser user) {
        super(from, to);
        this.gameUser = user;
    }

    @Override
    protected void exec(GameMechanics gameMechanics) {
        gameMechanics.removeUser(gameUser);
    }
}
