package frontend;

import base.AccountService;
import base.UserService;
import base.datasets.UserDataSet;
import com.sun.istack.internal.Nullable;
import dbservice.DatabaseException;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class GameWebSocketCreator implements WebSocketCreator {
    private static final Logger LOGGER = LogManager.getLogger(GameWebSocketCreator.class);
    final Context context;
    private final AccountService accountService;
    private final UserService userService;

    public GameWebSocketCreator(Context context) {
        this.context = context;
        this.accountService = (AccountService) context.get(AccountService.class);
        this.userService = (UserService) context.get(UserService.class);
    }

    @Override
    @Nullable
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        final String sessionId = req.getHttpServletRequest().getSession().getId();

        final Long userId = accountService.getUserIdBySesssion(sessionId);
        if (userId == null) {
            LOGGER.error("Unauthorized user tried to create a websocket");
            return null;
        }

        final UserDataSet userData;
        try {
            userData = userService.getUserById(userId);
        } catch (DatabaseException e) {
            LOGGER.error("Can't find info about user (for creating socket) with id {}", userId);
            return null;
        }
        return new GameWebSocket(userData, context);
    }
}

