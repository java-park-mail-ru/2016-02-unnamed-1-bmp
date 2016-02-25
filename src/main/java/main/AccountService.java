package main;

import java.util.HashMap;
import java.util.Map;


public class AccountService {
    private static volatile AccountService instance;
    private Map<String, UserProfile> users = new HashMap<>();
    private Map<Integer, UserProfile> idUsers = new HashMap<>();
    private Map<String, UserProfile> sessions = new HashMap<>();
    private Integer idCounter = 0;

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

    public UserProfile createteUser(String newUserName, String newPass, String newEmail) {
        if (users.containsKey(newUserName))
            return null;
        UserProfile profile = new UserProfile(idCounter, newUserName, newPass, newEmail);
        users.put(newUserName, profile);
        idUsers.put(idCounter++, profile);
        return profile;
    }

    public UserProfile getUserById(Integer userId){
        return idUsers.get(userId);
    }

    public boolean updateUser(Integer userId, String newUserName,
                              String newPass, String newEmail) {
        UserProfile user = getUserById(userId);
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

    public boolean deleteUser(Integer userId){
        UserProfile currUser = idUsers.get(userId);
        if (currUser == null)
            return false;
        users.remove(currUser.getLogin());
        idUsers.remove(currUser.getId());
        sessions.remove(currUser.getLogin());
        currUser.setDeleted();
        return true;
    }
}
