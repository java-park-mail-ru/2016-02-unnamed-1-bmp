package messagesystem;

import base.WebSocketService;
import game.GameMechanics;

import java.util.ArrayList;
import java.util.List;

public final class AddressService {
    private Address webSocketService;
    private List<Address> gameMechanicsList = new ArrayList<>();

    public void registerGameMechanics(GameMechanics gameMechanics) {
        gameMechanicsList.add(gameMechanics.getAddress());
    }

    public void registerWebSocketService(WebSocketService webSocketService) {
        this.webSocketService = webSocketService.getAddress();
    }

    public Address getWebSocketServiceAddress() {
        return webSocketService;
    }


    public synchronized Address getGameMechanicsAddressFor(String userName) {
        final int index = Math.abs(userName.hashCode()) % gameMechanicsList.size();
        return gameMechanicsList.get(index);
    }
}
