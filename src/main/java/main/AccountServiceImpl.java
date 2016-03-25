package main;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import base.AccountService;

public class AccountServiceImpl implements AccountService {
    private Map<String, Long> sessions = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void addSessions(String sessionId, Long userId) {
        if (!sessions.containsKey(sessionId)) {
            sessions.put(sessionId, userId);
            LOGGER.info("Added logged in user '{}'", userId);
        } else {
            LOGGER.error("Adding logged in user");
        }
    }

    @Override
    public Long getUserIdBySesssion(String sessionId) {
        return sessions.get(sessionId);
    }


    @Override
    public boolean logout(String sessionId) {
        if (sessions.get(sessionId) == null)
            return false;
        sessions.remove(sessionId);
        return true;
    }

    @Override
    public boolean userLoggedIn(String sessionId) {
        return sessions.get(sessionId) != null;
    }
}
