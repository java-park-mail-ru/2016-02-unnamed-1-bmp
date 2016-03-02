package main;

import java.util.HashMap;
import java.util.Map;


public class AccountService {
    private static volatile AccountService instance;
    private Map<String, UserProfile> users = new HashMap<>();
    private Map<Long, UserProfile> idUsers = new HashMap<>();
    private Map<String, UserProfile> sessions = new HashMap<>();
    private Long idCounter = 0L;

    public static AccountService getInstance() {
        AccountService localInstance = instance;
        if (localInstance == null) {
            synchronized (AccountService.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new AccountService();
                }
            }
        }
        return localInstance;
    }

    public UserProfile createUser(String newUserName, String newPass, String newEmail) {
        if (users.containsKey(newUserName))
            return null;
        final UserProfile profile = new UserProfile(idCounter, newUserName, newPass, newEmail);
        users.put(newUserName, profile);
        idUsers.put(idCounter++, profile);
        return profile;
    }

    public UserProfile getUserById(Long userId){
        return idUsers.get(userId);
    }

    public boolean updateUser(Long userId, String newUserName,
                              String newPass, String newEmail) {
        final UserProfile user = getUserById(userId);
        if (user == null)
            return false;
        user.updateProfile(newUserName, newPass, newEmail);
        return true;
    }

    public void addSessions(String sessionId, UserProfile userProfile) {
        sessions.put(sessionId, userProfile);
    }

    public UserProfile getUser(String userName) {
        return users.get(userName);
    }

    public UserProfile getSessions(String sessionId) {
        return sessions.get(sessionId);
    }

    public boolean deleteUser(Long userId){
        final UserProfile currUser = idUsers.get(userId);
        if (currUser == null)
            return false;
        users.remove(currUser.getLogin());
        idUsers.remove(currUser.getId());
        sessions.remove(currUser.getLogin());
        currUser.setDeleted();
        return true;
    }

    public boolean checkPassword(String login, String password) {
        final UserProfile currUser = users.get(login);
        return currUser.getPassword().equals(password);
    }

    public boolean getUserBySession(Long currentUserId,String sessionId) {
        final long toChangeUserId = sessions.get(sessionId).getId();
        return currentUserId.equals(toChangeUserId);
    }

    public boolean deleteUserSession(String sessionId) {
        if (sessions.get(sessionId) == null)
            return false;
        sessions.remove(sessionId);
        return true;
    }
}
