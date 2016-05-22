package frontend.messages;

import base.WebSocketService;
import frontend.GameWebSocket;
import messagesystem.Address;

public final class MessageAddSocket extends MessageToWebSocketService {
    private final GameWebSocket gameWebSocket;

    public MessageAddSocket(Address from, Address to, GameWebSocket webSocket) {
        super(from, to);
        gameWebSocket = webSocket;
    }

    @Override
    protected void exec(WebSocketService webSocketService) {
        webSocketService.addSocket(gameWebSocket);
    }
}
