package messagesystem;

import org.jetbrains.annotations.Nullable;

public abstract class Message {
    private final Address from;
    private final Address to;

    public Message(Address from, @Nullable Address to) {
        this.from = from;
        this.to = to;
    }

    public Address getFrom() {
        return from;
    }

    @Nullable
    public Address getTo() {
        return to;
    }

    public abstract void exec(Abonent abonent);
}
