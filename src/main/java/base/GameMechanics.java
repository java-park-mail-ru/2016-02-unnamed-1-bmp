package base;


import java.util.Map;

public interface GameMechanics {
    void addUser(String user, Map<String, String> userBoats);

    void run();

    void shoot(String myName, String coordinates);
}
