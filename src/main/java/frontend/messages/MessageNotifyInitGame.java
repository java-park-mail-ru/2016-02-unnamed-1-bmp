package frontend.messages;

import base.WebSocketService;
import game.GameUser;
import messagesystem.Address;

public class MessageNotifyInitGame extends MessageToWebSocketService {
    private final GameUser gameUser;
    private final boolean ok;
    private final Long gameSessionId;

    public MessageNotifyInitGame(Address from, Address to, GameUser gameUser,
                                 boolean ok, Long gameSessionId) {
        super(from, to);
        this.gameUser = gameUser;
        this.ok = ok;
        this.gameSessionId = gameSessionId;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.notifyInitGame(gameUser, ok, gameSessionId);
    }

}
