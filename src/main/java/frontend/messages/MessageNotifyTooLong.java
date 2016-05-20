package frontend.messages;

import base.WebSocketService;
import game.GameUser;
import messagesystem.Address;

public class MessageNotifyTooLong extends MessageToWebSocketService {
    private GameUser gameUser;

    public MessageNotifyTooLong(Address from, Address to, GameUser gameUser) {
        super(from, to);
        this.gameUser = gameUser;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.notifyTooLong(gameUser);
    }
}