package base;


public interface AccountService {

    void addSessions(String sessionId, Long userId);

    boolean logout(String sessionId);

    Long getUserIdBySesssion(String sessionId);

    boolean userLoggedIn(String sessionId);
}
