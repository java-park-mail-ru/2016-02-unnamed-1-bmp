package frontend.messages;

import base.WebSocketService;
import game.GameUser;
import messagesystem.Address;

public class MessageRemoveSocketByUser extends MessageToWebSocketService {
    private final GameUser gameUser;


    public MessageRemoveSocketByUser(Address from, Address to, GameUser gameUser) {
        super(from, to);
        this.gameUser = gameUser;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.removeSocketByUser(gameUser);
    }

}
