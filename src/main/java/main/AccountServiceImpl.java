package main;

import java.util.HashMap;
import java.util.Map;

import base.AccountService;

public class AccountServiceImpl implements AccountService {
    private Map<String, Long> sessions = new HashMap<>();

    @Override
    public void addSessions(String sessionId, Long userId) {
        sessions.put(sessionId, userId);
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
