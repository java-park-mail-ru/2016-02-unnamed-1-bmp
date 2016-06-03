package frontend.messages;

import base.WebSocketService;
import game.GameFieldShootResult;
import game.GameUser;
import messagesystem.Address;


public class MessageNotifyShootResult extends MessageToWebSocketService {
    private final GameUser gameUser;
    private final GameFieldShootResult result;
    private final boolean isMine;

    public MessageNotifyShootResult(Address from, Address to,
                                    GameUser gameUser, GameFieldShootResult result,
                                    boolean isMine) {
        super(from, to);
        this.gameUser = gameUser;
        this.result = result;
        this.isMine = isMine;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.notifyShootResult(gameUser, result, isMine);
    }
}