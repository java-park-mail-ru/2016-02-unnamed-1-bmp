package frontend;

import base.AccountService;
import base.UserService;
import dbservice.DatabaseException;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import java.util.Random;


public class GameWebSocketCreator implements WebSocketCreator {
    final Context context;
    private final AccountService accountService;
    private final UserService userService;
    private static final Logger LOGGER = LogManager.getLogger(GameWebSocketCreator.class);

    public enum AnimalPlayer {
        CAMEL,
        CHAMELEON,
        CHUPACABRA,
        COYOTE,
        ELEPHANT,
        FROG,
        GIRAFFE,
        GRIZZLY,
        HIPPO,
        HYENA,
        IGUANA,
        LEMUR,
        MONKEY,
        PANDA,
        PYTHON,
        SHEEP,
        TURTLE,
        WOLF;

        private static final int SIZE = AnimalPlayer.values().length;
        private static final Random RANDOM = new Random();

        public static AnimalPlayer randomAnimal() {
            return AnimalPlayer.values()[RANDOM.nextInt(SIZE)];
        }
    }

    public GameWebSocketCreator(Context context) {
        this.context = context;
        this.accountService = (AccountService) context.get(AccountService.class);
        this.userService = (UserService) context.get(UserService.class);
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        final String sessionId = req.getHttpServletRequest().getSession().getId();
        String currUserName = "ANONYMOUS " + AnimalPlayer.randomAnimal();
        long currUserId = -1;
        if (accountService.getUserIdBySesssion(sessionId) != null) {
            currUserId = accountService.getUserIdBySesssion(sessionId);
            try {
                currUserName = userService.getUserById(currUserId).getLogin();
            } catch (DatabaseException e) {
                LOGGER.error("Can't find info about user with id#{}", currUserId);
            }
        }
        LOGGER.info("Socket created for {}", currUserName);
        return new GameWebSocket(currUserName, context, currUserId);
    }
}

