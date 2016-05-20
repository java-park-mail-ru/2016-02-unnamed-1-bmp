package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messagesystem.Address;

public class MessageRemoveSocket extends MessageToWebSocketService {
    private GameWebSocket gameWebSocket;

    public MessageRemoveSocket(Address from, Address to, GameWebSocket webSocket) {
        super(from, to);
        gameWebSocket = webSocket;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.removeSocket(gameWebSocket);
    }
}
