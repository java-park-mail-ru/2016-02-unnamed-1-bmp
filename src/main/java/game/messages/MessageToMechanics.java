package game.messages;

import game.GameMechanics;
import messagesystem.Abonent;
import messagesystem.Address;
import messagesystem.Message;
import org.jetbrains.annotations.Nullable;

public abstract class MessageToMechanics extends Message {
    public MessageToMechanics(Address from, @Nullable Address to) {
        super(from, to);
    }

    @Override
    public void exec(Abonent abonent) {
        if (abonent instanceof GameMechanics) {
            exec((GameMechanics) abonent);
        }
    }

    protected abstract void exec(GameMechanics gameMechanics);
}