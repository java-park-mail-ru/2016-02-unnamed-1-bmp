package frontend.messages;

import base.WebSocketService;
import game.GameUser;
import messagesystem.Address;

public class MessageNotifyGameOver extends MessageToWebSocketService {
    private GameUser gameUser;
    private boolean win;

    public MessageNotifyGameOver(Address from, Address to,
                                 GameUser gameUser, boolean win) {
        super(from, to);
        this.gameUser = gameUser;
        this.win = win;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.notifyGameOver(gameUser, win);
    }
}