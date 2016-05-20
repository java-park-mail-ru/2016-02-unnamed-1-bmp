package frontend.messages;

import base.WebSocketService;
import game.GameUser;
import messagesystem.Address;

public class MessageNotifyStart extends MessageToWebSocketService {
    private GameUser gameUser;
    private GameUser opponent;

    public MessageNotifyStart(Address from, Address to,
                              GameUser gameUser, GameUser opponents) {
        super(from, to);
        this.opponent = opponents;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.notifyStartGame(gameUser, opponent);
    }

}