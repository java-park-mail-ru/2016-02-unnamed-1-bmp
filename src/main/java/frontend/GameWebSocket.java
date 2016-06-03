package frontend;

import base.*;
import base.datasets.UserDataSet;
import com.google.gson.*;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.jetbrains.annotations.NotNull;
import frontend.messages.MessageAddSocket;
import frontend.messages.MessageOpponentOnline;
import frontend.messages.MessageRemoveSocket;
import game.*;
import game.messages.*;
import main.Context;
import messagesystem.Abonent;
import messagesystem.Address;
import messagesystem.MessageSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

@WebSocket
public class GameWebSocket implements Abonent {
    private static final Logger LOGGER = LogManager.getLogger(GameWebSocket.class);
    private final Address address = new Address();
    private final MessageSystem messageSystem;
    private final UserDataSet user;
    private final UserService userService;
    private Session session;

    private final ConcurrentLinkedQueue<String> sendQueue = new ConcurrentLinkedQueue<>();
    private boolean isActiveSendQueue = false;

    GameWebSocket(@NotNull UserDataSet user, Context context) {
        this.user = user;
        this.messageSystem = (MessageSystem) context.get(MessageSystem.class);
        this.userService = (UserService) context.get(UserService.class);
    }

    public UserDataSet getUser() {
        return this.user;
    }

    @SuppressWarnings("unused")
    @OnWebSocketConnect
    public void onOpen(Session ses) {
        this.session = ses;
        LOGGER.info("Opened web socket, user id {}", this.user.getId());
        messageSystem.sendMessage(new MessageAddSocket(this.address,
                messageSystem.getAddressService().getWebSocketServiceAddress(), this));

        messageSystem.sendMessage(new MessageGetGameUserAndSessionFor(this.address,
                messageSystem.getAddressService().getGameMechanicsAddressFor(this.user),
                this.user, (gameUser, gameSession) -> {
            if (gameUser != null && gameSession != null) {
                gameUser.setOnline();
                final GameUser opponent = gameSession.getOpponent(gameUser);
                if (opponent != null) {
                    messageSystem.sendMessage(new MessageOpponentOnline(this.address,
                            messageSystem.getAddressService().getWebSocketServiceAddress(),
                            opponent, gameUser));
                }
            }
        }));
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        LOGGER.info("Closed web socket, user id {}", this.user.getId());
        messageSystem.sendMessage(new MessageRemoveSocket(this.address,
                messageSystem.getAddressService().getWebSocketServiceAddress(), this));

        messageSystem.sendMessage(new MessageGetGameUserAndSessionFor(this.address,
                messageSystem.getAddressService().getGameMechanicsAddressFor(this.user),
                this.user, (gameUser, gameSession) -> {
            if (gameUser != null && gameSession != null) {
                gameUser.setOffline();

                final GameUser opponent = gameSession.getOpponent(gameUser);
                if (opponent != null) {
                    messageSystem.sendMessage(new MessageOpponentOnline(this.address,
                            messageSystem.getAddressService().getWebSocketServiceAddress(),
                            opponent, gameUser));
                }
            }
        }));
    }

    @SuppressWarnings("unused")
    @OnWebSocketMessage
    public void onMessage(String data) {
        LOGGER.info("Message in socket, user id {}: \"{}\"", this.user.getId(), data);

        try {
            final JsonElement jsonElement = new JsonParser().parse(data);
            final String action = jsonElement.getAsJsonObject().getAsJsonPrimitive("action").getAsString();
            if (action == null) {
                throw new JsonSyntaxException("No action field in web socket message");
            }
            LOGGER.info("Message in web socket, user id {}: action is {}", this.user.getId(), action);

            final JsonObject message = jsonElement.getAsJsonObject();

            switch (action) {
                case "getGameStatus":
                    this.onMessageGetGameStatus();
                    break;
                case "initNewGame":
                    this.onMessageInitNewGame(message);
                    break;
                case "giveUp":
                    this.onMessageGiveUp();
                    break;
                case "shoot":
                    this.onMessageShoot(message);
                    break;
                default:
                    throw new JsonSyntaxException("Unknown action");
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error("Json Syntax error while onMessage", e);
            this.send(new GameWebSocketMessage(GameWebSocketMessage.MessageType.ERROR, "Wrong JSON: " + e.getMessage()));
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected error while onMessage", e);
            this.send(new GameWebSocketMessage(GameWebSocketMessage.MessageType.ERROR, "Unexpected error"));
        }
    }

    public void onMessageGetGameStatus() {
        messageSystem.sendMessage(new MessageGetGameUserAndSessionFor(this.address,
                messageSystem.getAddressService().getGameMechanicsAddressFor(this.user),
                this.user, (gameUser, gameSession) -> {
            final GameWebSocketMessage result = new GameWebSocketMessage(GameWebSocketMessage.MessageType.GAME_STATUS);

            if (gameSession == null) {
                result.setOk(false);
                this.send(result.getAsJSON());
                return;
            }
            result.setId(gameSession.getId());
            final JsonObject resultJson = result.getAsJSON();
            resultJson.add("started", new JsonPrimitive(gameSession.isStarted()));
            final JsonArray shipsJson = this.collectShips(gameUser, true);
            resultJson.add("ships", shipsJson);
            final JsonArray killedShipsJson = this.collectShips(gameUser, false);
            resultJson.add("killedShips", killedShipsJson);
            final JsonArray shootsJson = this.collectShoots(gameUser);
            resultJson.add("shoots", shootsJson);

            if (gameSession.isStarted()) {
                resultJson.add("turn", new JsonPrimitive(gameSession.isTurnOf(gameUser)));
                final GameUser opponent = gameSession.getOpponent(gameUser);
                if (opponent != null) {
                    final String opponentName = opponent.getName();
                    resultJson.add("opponentName", new JsonPrimitive(opponentName));
                    final JsonArray opponentShipsJson = this.collectShips(opponent, false);
                    resultJson.add("opponentShips", opponentShipsJson);
                    final JsonArray opponentShootsJson = this.collectShoots(opponent);
                    resultJson.add("opponentShoots", opponentShootsJson);
                }
            }

            this.send(resultJson);
        }));
    }

    private JsonArray collectShips(GameUser gameUser, boolean all) {
        final JsonArray shipsJson = new JsonArray();
        Stream<GameFieldShip> ships = gameUser.getField().getShips().stream();
        if (!all) {
            ships = ships.filter(GameFieldShip::isKilled);
        }

        ships.forEach(ship -> {
            final JsonArray shipJson = new JsonArray();
            shipJson.add(ship.getX());
            shipJson.add(ship.getY());
            shipJson.add(ship.getLength());
            shipJson.add(ship.isVertical());
            shipsJson.add(shipJson);
        });
        return shipsJson;
    }

    private JsonArray collectShoots(GameUser gameUser) {
        final JsonArray shootsJson = new JsonArray();
        gameUser.getField().getShoots().forEach(shoot -> {
            final JsonArray shootJson = new JsonArray();
            shootJson.add(shoot.getX());
            shootJson.add(shoot.getY());
            shootJson.add(!shoot.isMiss());
            shootsJson.add(shootJson);
        });
        return shootsJson;
    }

    public void onMessageInitNewGame(JsonObject message) {
        final String gameMode = message.getAsJsonPrimitive("mode").getAsString();
        final GameFieldProperties gameFieldProperties = GameFieldProperties.getProperties();

        if (gameFieldProperties == null) {
            this.send(new GameWebSocketMessage(GameWebSocketMessage.MessageType.ERROR, "Unexpected error"));
            return;
        }

        final GameField gameField = new GameField(gameFieldProperties);

        message.getAsJsonArray("ships").forEach((ship) -> {
            final int x = ship.getAsJsonArray().get(0).getAsInt();
            final int y = ship.getAsJsonArray().get(1).getAsInt();
            final int length = ship.getAsJsonArray().get(2).getAsInt();
            final boolean isVertical = ship.getAsJsonArray().get(3).getAsBoolean();

            final GameFieldShip shipObj = new GameFieldShip(x, y, length, isVertical);

            gameField.addShip(shipObj);
        });

        final GameUser gameUser = new GameUser(this.user, gameField, userService);

        if (!gameField.isValid()) {
            this.send(new GameWebSocketMessage(GameWebSocketMessage.MessageType.ERROR, "Game field is invalid"));
            return;
        }

        switch (gameMode) {
            case "bot":
                messageSystem.sendMessage(new MessageAddUserForBotGame(this.address,
                        messageSystem.getAddressService().getGameMechanicsAddressFor(this.user),
                        gameUser));
                break;
            case "friend":
                final JsonElement gameSessionIdElement = message.getAsJsonPrimitive("id");
                final Long gameSessionId = gameSessionIdElement == null ? null : gameSessionIdElement.getAsLong();
                messageSystem.sendMessage(new MessageAddUserForFriendGame(this.address,
                        messageSystem.getAddressService().getGameMechanicsAddressFor(this.user),
                        gameUser, gameSessionId));
                break;
            case "random":
            default:
                messageSystem.sendMessage(new MessageAddUserForRandomGame(this.address,
                        messageSystem.getAddressService().getGameMechanicsAddressFor(this.user),
                        gameUser));
                break;
        }
    }

    public void onMessageGiveUp() {
        messageSystem.sendMessage(new MessageGetGameUserAndSessionFor(this.address,
                messageSystem.getAddressService().getGameMechanicsAddressFor(this.user),
                this.user, (gameUser, gameSession) -> {
            if (gameSession != null) {
                gameSession.giveUp(gameUser);
            }
        }));
    }

    public void onMessageShoot(JsonObject message) {
        messageSystem.sendMessage(new MessageGetGameUserAndSessionFor(this.address,
                messageSystem.getAddressService().getGameMechanicsAddressFor(this.user),
                this.user, (gameUser, gameSession) -> {
            if (gameSession != null && gameUser != null) {
                final int x = message.getAsJsonPrimitive("x").getAsInt();
                final int y = message.getAsJsonPrimitive("y").getAsInt();
                gameSession.shoot(gameUser, x, y, null);
            }
        }));
    }

    public void send(String message) {
        this.sendQueue.add(message);
        this.sendNextMessage();
    }

    private void sendNextMessage() {
        if (this.isActiveSendQueue) {
            return;
        }

        final String message = this.sendQueue.peek();
        if (message == null) return;

        try {
            final RemoteEndpoint remote = this.session.getRemote();
            this.isActiveSendQueue = true;
            remote.sendString(message, new SendCallback(this, message));
        } catch (WebSocketException e) {
            LOGGER.error("Couldn't get remote endpoint for web socket, user id {}", this.user.getId());
        }
    }

    public void send(JsonObject message) {
        this.send(message.toString());
    }

    public void send(GameWebSocketMessage message) {
        this.send(message.toString());
    }

    public void close() {
        LOGGER.info("Attempting to close web socket, user id {}", this.user.getId());
        if (session.isOpen()) {
            this.session.close();
        }
    }

    public boolean isOpen() {
        return this.session.isOpen();
    }

    @Override
    public Address getAddress() {
        return address;
    }

    private static class SendCallback implements WriteCallback {
        private final GameWebSocket gameWebSocket;
        private final String message;

        SendCallback(GameWebSocket gameWebSocket, String message) {
            this.gameWebSocket = gameWebSocket;
            this.message = message;
        }

        @Override
        public void writeFailed(Throwable e) {
            LOGGER.info("Fail to send a message in websocket, user id {}, {}", this.gameWebSocket.getUser().getId(), this.message);
            LOGGER.error("Fail to send a message in websocket", e);
            this.gameWebSocket.isActiveSendQueue = false;
        }

        @Override
        public void writeSuccess() {
            LOGGER.info("Success send a message in websocket, user id {}, {}", this.gameWebSocket.getUser().getId(), this.message);
            this.gameWebSocket.sendQueue.poll();
            this.gameWebSocket.isActiveSendQueue = false;
            this.gameWebSocket.sendNextMessage();
        }
    }
}

