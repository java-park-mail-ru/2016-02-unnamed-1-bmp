package base;


import java.util.Map;

public interface GameMechanics {
    void addUser(String user, Map<String, String> userBoats);

    void removeUser(String user);

    void run();

    void shoot(String myName, String coordinates);
}
