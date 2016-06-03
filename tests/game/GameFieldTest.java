package game;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameFieldTest {

    @Test
    public void testGenerateRandomField() throws Exception {
        final GameFieldProperties props = GameFieldProperties.getProperties();
        if (props == null) return;

        for (int i = 0; i < 1000; i++) {
            final GameField gameField = GameField.generateRandomField(props);
            assertTrue(gameField.isValid());
        }
    }

    @Test
    public void testGetProperties() throws Exception {
        final GameFieldProperties props = GameFieldProperties.getProperties();
        if (props == null) return;

        final GameField gameField = new GameField(props);
        assertEquals(props, gameField.getProperties());

        final GameField gameFieldRandom = GameField.generateRandomField(props);
        assertEquals(props, gameFieldRandom.getProperties());
    }

    @Test
    public void testIsValidShip() throws Exception {
        final GameFieldProperties props = GameFieldProperties.getProperties();
        if (props == null) return;

        final GameField gameField = new GameField(props);

        final GameFieldShip ship = new GameFieldShip(1, 1, props.getMaxDeck() + 1, true);
        assertFalse(gameField.addShip(ship));

        final GameFieldShip ship1 = new GameFieldShip(1, 1, props.getMaxDeck(), true);
        assertTrue(gameField.addShip(ship1));

        final GameFieldShip ship2 = new GameFieldShip(2, 2, props.getMaxDeck(), true);
        assertFalse(gameField.addShip(ship2));

        final GameField gameField1 = new GameField(props);
        Integer x = 1;

        while(gameField1.countShips(props.getMaxDeck()) < props.getShips(props.getMaxDeck())) {
            assertTrue(gameField1.addShip(new GameFieldShip(x, 1, props.getMaxDeck(), true)));

            x+= 2;
        }

        assertFalse(gameField.addShip(new GameFieldShip(x, 1, props.getMaxDeck(), true)));
    }

    @Test
    public void testShootIsKilled() throws Exception {
        final GameFieldProperties props = GameFieldProperties.getProperties();
        if (props == null) return;

        final GameField gameField = GameField.generateRandomField(props);

        gameField.getShips().forEach((ship) -> {
            ship.getDecks().forEach((deck) -> {
                final GameFieldShootResult res = gameField.shoot(deck.getX(), deck.getY());
                assertTrue(res.isKilled() || res.isWound());
            });
            assertTrue(ship.isKilled());
        });

        assertTrue(gameField.isKilled());
    }

    @Test
    public void testIsValid() throws Exception {
        final GameFieldProperties props = GameFieldProperties.getProperties();
        if (props == null) return;

        final GameField gameField = GameField.generateRandomField(props);
        final GameField testGameField = new GameField(props);

        assertTrue(gameField.isValid());

        final ArrayList<GameFieldShip> ships = gameField.getShips();

        for(int i = 0; i < ships.size(); i++) {
            testGameField.addShip(ships.get(i));
            assertEquals(testGameField.isValid(), i >= ships.size() - 1);
        }
    }
}