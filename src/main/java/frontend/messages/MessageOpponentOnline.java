package frontend.messages;

import base.WebSocketService;
import game.GameUser;
import messagesystem.Address;


public class MessageOpponentOnline extends MessageToWebSocketService {
    private GameUser opponent;
    private GameUser current;

    public MessageOpponentOnline(Address from, Address to, GameUser opponent, GameUser current) {
        super(from, to);
        this.current = current;
        this.opponent = opponent;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.notifyOpponentOnline(current, opponent);
    }
}
