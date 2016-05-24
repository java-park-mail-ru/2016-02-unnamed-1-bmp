package game.messages;

import base.datasets.UserDataSet;
import game.GameMechanics;
import game.GameSession;
import game.GameUser;
import messagesystem.Address;

import java.util.function.BiConsumer;

public class MessageGetGameUserAndSessionFor extends MessageToMechanics {
    private final UserDataSet user;
    private final BiConsumer<GameUser, GameSession> purpose;

    public MessageGetGameUserAndSessionFor(Address from, Address to, UserDataSet user, BiConsumer<GameUser, GameSession> purpose) {
        super(from, to);
        this.user = user;
        this.purpose = purpose;
    }

    @Override
    protected void exec(GameMechanics gameMechanics) {
        final GameUser gameUser = gameMechanics.getGameUser(this.user);
        final GameSession gameSession = gameMechanics.getUserGameSession(this.user);

        this.purpose.accept(gameUser, gameSession);
    }
}
