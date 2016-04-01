package base;


public interface AccountService {

    void addSessions(String sessionId, Long userId);

    boolean logout(String sessionId);

    void logoutFull(long userId);

    Long getUserIdBySesssion(String sessionId);

    boolean userLoggedIn(String sessionId);
}
