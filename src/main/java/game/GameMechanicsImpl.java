package game;

import base.AnimalPlayer;
import base.datasets.UserDataSet;
import frontend.messages.MessageNotifyInitGame;
import frontend.messages.MessageRemoveSocketByUser;
import messagesystem.Address;
import messagesystem.MessageSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import main.Context;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class GameMechanicsImpl implements GameMechanics {
    private static final int STEP_TIME = 200;
    private static final int STEP_COUNT_EXTENDED = 20;
    private static final Logger LOGGER = LogManager.getLogger(GameMechanicsImpl.class);

    private final Address address = new Address();
    private final MessageSystem messageSystem;
    private final Context context;

    private final List<Long> waiters = Collections.synchronizedList(new LinkedList<>());
    private final Map<Long, GameUser> gameUsers = new ConcurrentHashMap<>(); // Long = User ID
    private final Map<Long, GameSession> gameSessions = new ConcurrentHashMap<>(); // Long = GameSession Id
    private final Map<Long, GameSession> usersToSessions = new ConcurrentHashMap<>(); // Long = User ID

    public GameMechanicsImpl(Context context) {
        this.context = context;
        this.messageSystem = (MessageSystem) context.get(MessageSystem.class);
    }

    @Nullable
    @Override
    public GameSession getUserGameSession(UserDataSet user) {
        final long userId = user.getId();
        if (this.usersToSessions.containsKey(userId)) {
            return this.usersToSessions.get(userId);
        }
        return null;
    }

    @Override
    public boolean hasUserGameSession(UserDataSet user) {
        return this.getUserGameSession(user) != null;
    }

    @Override
    @Nullable
    public GameUser getGameUser(UserDataSet user) {
        return this.gameUsers.get(user.getId());
    }

    @Override
    public void addUserForRandomGame(GameUser gameUser, GameSession gameSession) {
        LOGGER.info("Attempting to add user to random game, user {}", gameUser.getName());
        if (gameUser.getUser() == null || this.hasUserGameSession(gameUser.getUser())) {
            messageSystem.sendMessage(new MessageNotifyInitGame(this.address,
                    messageSystem.getAddressService().getWebSocketServiceAddress(), gameUser, false, null));
            return;
        }

        if (this.waiters.isEmpty()) {
            if (gameSession.addUser(gameUser)) {
                LOGGER.info("Add user to random game (to waiters), user {}, game session id {}",
                        gameUser.getName(), gameSession.getId());
                final long userId = gameUser.getUser().getId();
                this.waiters.add(gameUser.getUser().getId());
                this.gameUsers.put(userId, gameUser);
                this.gameSessions.put(gameSession.getId(), gameSession);
                this.usersToSessions.put(userId, gameSession);
                messageSystem.sendMessage(new MessageNotifyInitGame(this.address,
                        messageSystem.getAddressService().getWebSocketServiceAddress(), gameUser, true, null));

                return;
            }
        } else {
            final long userId = this.waiters.remove(0);
            final GameSession checkGameSession = this.usersToSessions.get(userId);

            if (checkGameSession.addUser(gameUser)) {
                LOGGER.info("Add user to random game, user {}, game session id {}",
                        gameUser.getName(), checkGameSession.getId());
                this.gameUsers.put(gameUser.getUser().getId(), gameUser);
                this.usersToSessions.put(gameUser.getUser().getId(), checkGameSession);
                messageSystem.sendMessage(new MessageNotifyInitGame(this.address,
                        messageSystem.getAddressService().getWebSocketServiceAddress(), gameUser, true, null));
                checkGameSession.start();
                return;
            } else {
                this.waiters.add(userId);
            }
        }
        messageSystem.sendMessage(new MessageNotifyInitGame(this.address,
                messageSystem.getAddressService().getWebSocketServiceAddress(), gameUser, false, null));
    }

    @Override
    public void addUserForBotGame(GameUser gameUser, GameSession gameSession) {
        LOGGER.info("Attempting to add user to bot game, user {}", gameUser.getName());
        if (gameUser.getUser() == null || this.hasUserGameSession(gameUser.getUser())) {
            messageSystem.sendMessage(new MessageNotifyInitGame(this.address,
                    messageSystem.getAddressService().getWebSocketServiceAddress(), gameUser, false, null));
            return;
        }

        if (gameSession.addUser(gameUser)) {
            LOGGER.info("Add user to bot game, user {}, game session id {}",
                    gameUser.getName(), gameSession.getId());
            this.gameUsers.put(gameUser.getUser().getId(), gameUser);
            this.usersToSessions.put(gameUser.getUser().getId(), gameSession);

            final GameUser botUser = new GameUser(AnimalPlayer.randomAnimal(),
                    GameField.generateRandomField(gameSession.getGameFieldProperties()));
            botUser.setBotHelper(new GameUserBotHelper(gameSession, botUser));
            gameSession.addUser(botUser);

            messageSystem.sendMessage(new MessageNotifyInitGame(this.address,
                    messageSystem.getAddressService().getWebSocketServiceAddress(), gameUser, true, null));
            gameSession.start();
            return;
        }

        messageSystem.sendMessage(new MessageNotifyInitGame(this.address,
                messageSystem.getAddressService().getWebSocketServiceAddress(), gameUser, false, null));
    }

    @Override
    public void addUserForFriendGame(GameUser gameUser, @Nullable Long gameSessionId) {
        LOGGER.info("Attempting to add user to friend game, user {}, game session id {}",
                gameUser.getName(), gameSessionId);
        if (gameUser.getUser() == null || this.hasUserGameSession(gameUser.getUser())) {
            messageSystem.sendMessage(new MessageNotifyInitGame(this.address,
                    messageSystem.getAddressService().getWebSocketServiceAddress(), gameUser, false, null));
            return;
        }

        if (gameSessionId != null) {
            final GameSession gameSession = this.gameSessions.get(gameSessionId);
            if (gameSession != null) {
                if (gameSession.addUser(gameUser)) {
                    LOGGER.info("Add user to friend game, user {}, game session id {}",
                            gameUser.getName(), gameSession.getId());
                    final long userId = gameUser.getUser().getId();
                    this.gameUsers.put(userId, gameUser);
                    this.usersToSessions.put(userId, gameSession);

                    messageSystem.sendMessage(new MessageNotifyInitGame(this.address,
                            messageSystem.getAddressService().getWebSocketServiceAddress(),
                            gameUser, true, gameSession.getId()));
                    gameSession.start();
                    return;
                }
            }
        } else {
            final GameSession gameSession = new GameSession(this.context, gameUser.getFieldProperties());
            if (gameSession.addUser(gameUser)) {
                LOGGER.info("Add user to friend game (create new one), user {}, game session id {}",
                        gameUser.getName(), gameSession.getId());
                this.gameUsers.put(gameUser.getUser().getId(), gameUser);
                this.gameSessions.put(gameSession.getId(), gameSession);
                this.usersToSessions.put(gameUser.getUser().getId(), gameSession);

                messageSystem.sendMessage(new MessageNotifyInitGame(this.address,
                        messageSystem.getAddressService().getWebSocketServiceAddress(),
                        gameUser, true, gameSession.getId()));

                return;
            }
        }

        messageSystem.sendMessage(new MessageNotifyInitGame(this.address,
                messageSystem.getAddressService().getWebSocketServiceAddress(),
                gameUser, false, null));
    }

    @Override
    public void removeUser(GameUser gameUser) {
        if (gameUser.getUser() != null) {
            final long userId = gameUser.getUser().getId();
            this.gameUsers.remove(userId, gameUser);

            messageSystem.sendMessage(new MessageRemoveSocketByUser(this.address,
                    messageSystem.getAddressService().getWebSocketServiceAddress(), gameUser));

            final GameSession gameSession = this.usersToSessions.get(userId);
            if (gameSession != null) {
                LOGGER.info("Remove user, user {}, game session id {}", gameUser.getName(), gameSession.getId());
                this.gameSessions.remove(gameSession.getId(), gameSession);
                this.usersToSessions.remove(userId, gameSession);
            }
        }
    }


    private void timeStep(boolean extended) {
        this.gameSessions.forEach((sessionId, session) -> {
            session.checkTooLong();
            if (extended) {
                session.checkOffline();
            }
        });
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        long stepCounter = 0;
        while (true) {
            messageSystem.execForAbonent(this);
            try {
                timeStep(stepCounter++ % STEP_COUNT_EXTENDED == 0);
                Thread.sleep(STEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
