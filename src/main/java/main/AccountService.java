package main;

import java.util.HashMap;
import java.util.Map;


public class AccountService {
    private Map<String, UserProfile> users = new HashMap<>();
    private Map<String, UserProfile> sessions = new HashMap<>();

    public boolean addUser(String userName, UserProfile userProfile) {
        if (users.containsKey(userName))
            return false;
        users.put(userName, userProfile);
        return true;
    }

    public boolean updateUser(Integer userId, String newUserName,
                              String newPass, String newEmail) {
        //check user in database by id
        return addUser(newUserName, new UserProfile(newUserName, newPass, newEmail));
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

    public void deleteUser(String username){
        UserProfile currUser = users.get(username);
        currUser.setDeleted();
    }
}
