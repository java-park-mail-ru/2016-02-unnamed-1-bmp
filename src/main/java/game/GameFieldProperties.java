package game;

import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public final class GameFieldProperties {

    private static final HashMap<String, GameFieldProperties> LOADED = new HashMap<>();
    private Properties gameData = null;

    private GameFieldProperties(Properties gameData) {
        this.gameData = gameData;
    }

    @Nullable
    public static GameFieldProperties getProperties(String gameMode) {
        if (LOADED.containsKey(gameMode)) {
            return LOADED.get(gameMode);
        }

        try (final FileInputStream fileData = new FileInputStream("setups/game/" + gameMode + ".properties")) {
            final Properties gameData = new Properties();
            gameData.load(fileData);
            final GameFieldProperties gameObject = new GameFieldProperties(gameData);
            LOADED.put(gameMode, gameObject);
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
}