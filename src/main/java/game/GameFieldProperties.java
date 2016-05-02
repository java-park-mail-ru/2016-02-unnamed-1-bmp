package game;

import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public final class GameFieldProperties {

    private static HashMap<String, GameFieldProperties> loaded = new HashMap<>();
    private Properties gameData = null;

    private GameFieldProperties(Properties gameData) {
        this.gameData = gameData;
    }

    public int getSize() {
        return Integer.valueOf(this.gameData.getProperty("size"));
    }

    public int getMaxDeck() {
        return Integer.valueOf(this.gameData.getProperty("maxdeck"));
    }

    public int getShips(int decks) {
        final String count = this.gameData.getProperty("ship" + String.valueOf(decks));
        return count == null ? 0 : Integer.valueOf(count);
    }

    public int getDecks() {
        final int maxShipDecks = this.getMaxDeck();
        int res = 0;
        for (int i = 1; i <= maxShipDecks; i++) {
            res += i * this.getShips(i);
        }
        return res;
    }

    @Nullable
    public static GameFieldProperties getProperties(String gameMode) {
        if (loaded.containsKey(gameMode)) {
            return loaded.get(gameMode);
        }

        try (final FileInputStream fileData = new FileInputStream("setups/game/" + gameMode + ".properties")) {
            final Properties gameData = new Properties();
            gameData.load(fileData);
            final GameFieldProperties gameObject = new GameFieldProperties(gameData);
            loaded.put(gameMode, gameObject);
            return gameObject;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static GameFieldProperties getProperties() {
        return getProperties("10x10");
    }
}