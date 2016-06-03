package messagesystem;

import base.WebSocketService;
import base.datasets.UserDataSet;
import game.GameMechanics;

import java.util.ArrayList;
import java.util.List;

public final class AddressService {
    private final List<Address> gameMechanicsList = new ArrayList<>();
    private Address webSocketService;

    public void registerGameMechanics(GameMechanics gameMechanics) {
        gameMechanicsList.add(gameMechanics.getAddress());
    }

    public void registerWebSocketService(WebSocketService socketService) {
        this.webSocketService = socketService.getAddress();
    }

    public Address getWebSocketServiceAddress() {
        return webSocketService;
    }


    public synchronized Address getGameMechanicsAddressFor(UserDataSet user) {
        final int index = Math.abs(user.getLogin().hashCode()) % gameMechanicsList.size();
        return gameMechanicsList.get(index);
    }
}
