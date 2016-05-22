package frontend.messages;

import base.WebSocketService;
import game.GameUser;
import messagesystem.Address;


public class MessageNotifyTurn extends MessageToWebSocketService {
    private final GameUser gameUser;
    private final boolean itsTurn;

    public MessageNotifyTurn(Address from, Address to,
                             GameUser gameUser, boolean isHis) {
        super(from, to);
        this.itsTurn = isHis;
        this.gameUser = gameUser;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.notifyTurn(gameUser, itsTurn);
    }

}