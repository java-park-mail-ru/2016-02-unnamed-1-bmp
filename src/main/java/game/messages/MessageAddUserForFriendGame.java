package game.messages;

import game.GameMechanics;
import game.GameUser;
import messagesystem.Address;

public class MessageAddUserForFriendGame extends MessageToMechanics {
    private final GameUser gameUser;
    private final Long gameSessionId;

    public MessageAddUserForFriendGame(Address from, Address to,
                                       GameUser gameUser, Long gameSessionId) {
        super(from, to);
        this.gameUser = gameUser;
        this.gameSessionId = gameSessionId;
    }

    @Override
    protected void exec(GameMechanics gameMechanics) {
        gameMechanics.addUserForFriendGame(gameUser, gameSessionId);
    }
}
