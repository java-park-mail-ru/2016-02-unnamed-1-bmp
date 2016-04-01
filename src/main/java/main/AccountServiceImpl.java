package main;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import base.AccountService;

public class AccountServiceImpl implements AccountService {
    private Map<String, Long> sessions = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(AccountServiceImpl.class);

    @Override
    public void addSessions(String sessionId, Long userId) {
        if (!sessions.containsKey(sessionId)) {
            sessions.put(sessionId, userId);
            LOGGER.info("Added logged in user #{}", userId);
        } else {
            LOGGER.error("Fail to log in user");
        }
    }

    @Override
    public Long getUserIdBySesssion(String sessionId) {
        return sessions.get(sessionId);
    }


    @Override
    public boolean logout(String sessionId) {
        if (sessions.get(sessionId) == null) {
            LOGGER.error("Fail to log out user");
            return false;
        }
        sessions.remove(sessionId);
        LOGGER.info("Logged out user #{}", sessionId);
        return true;
    }

    @Override
    public boolean userLoggedIn(String sessionId) {
        return sessions.get(sessionId) != null;
    }

    @Override
    public void logoutFull(long userId) {
        sessions.values().removeAll(Collections.singleton(userId));
    }

}
