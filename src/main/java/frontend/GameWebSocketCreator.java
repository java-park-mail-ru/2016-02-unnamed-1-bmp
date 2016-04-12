package frontend;

import base.AccountService;
import base.UserService;
import base.WebSocketService;
import dbservice.DatabaseException;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;


public class GameWebSocketCreator implements WebSocketCreator {
    Context context;
    private AccountService accountService;
    private UserService userService;
    private static final Logger LOGGER = LogManager.getLogger(GameWebSocketCreator.class);
    int counter = 0;

    public GameWebSocketCreator(Context context) {
        this.context = context;
        this.accountService = (AccountService) context.get(AccountService.class);
        this.userService = (UserService) context.get(UserService.class);
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        final String sessionId = req.getHttpServletRequest().getSession().getId();
//        final long currUserId = accountService.getUserIdBySesssion(sessionId);
        ++counter;
        String currUserName = "unidentified koala #" + counter;
//        try { TODO check whether user logged in
//            currUserName = userService.getUserById(currUserId).getLogin();
//        } catch (DatabaseException e) {
//            e.printStackTrace();
//        }
        LOGGER.info("Socket created {}", currUserName);
        return new GameWebSocket(currUserName, context, sessionId);
    }
}

