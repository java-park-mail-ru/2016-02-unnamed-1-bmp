package frontend.messages;

import base.WebSocketService;
import game.GameUser;
import messagesystem.Address;


public class MessageOpponentOnline extends MessageToWebSocketService {
    private final GameUser opponent;
    private final GameUser current;

    public MessageOpponentOnline(Address from, Address to, GameUser opponent, GameUser current) {
        super(from, to);
        this.current = current;
        this.opponent = opponent;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.notifyOpponentOnline(this.opponent, this.current);
    }
}
