package game;

import org.hamcrest.core.StringContains;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameFieldShipTest {

    @Test
    public void testIsKilled() throws GameFieldShipException {
        final Integer x = 100;
        final Integer y = 100;
        final Integer length = 10;
        final GameFieldShip ship = new GameFieldShip(x, y, length, true);
        final GameFieldShip ship1 = new GameFieldShip(x, y, length, false);

        for(int i = y; i < y + length; i++) {
            ship.shoot(x, i);
            assertEquals(ship.isKilled(), i == y + length - 1);
        }

        for(int i = x; i < x + length; i++) {
            ship1.shoot(i, y);
            assertEquals(ship1.isKilled(), i == x + length - 1);
        }
    }

    @Test
    public void testContainsDeck() throws Exception {
        final Integer x = 100;
        final Integer y = 100;
        final Integer length = 10;
        final GameFieldShip ship = new GameFieldShip(x, y, length, true);

        for(int i = y - 1; i < y + length + 1; i++) {
            for(int j = x - 1; j < x + 2; j++) {
                assertEquals(ship.containsDeck(j, i), j == x && i >= y && i < y + length);
            }
        }
    }

    @Test
    public void testIntersects() throws Exception {
        final GameFieldShip ship = new GameFieldShip(10, 10, 4, false);

        final ArrayList<GameFieldShip> goodShips = new ArrayList<>();
        goodShips.add(new GameFieldShip(10, 8, 4, false));
        goodShips.add(new GameFieldShip(10, 12, 4, false));
        goodShips.add(new GameFieldShip(10, 12, 4, true));
        goodShips.add(new GameFieldShip(8, 10, 4, true));
        goodShips.add(new GameFieldShip(15, 10, 4, true));
        goodShips.add(new GameFieldShip(15, 10, 4, false));
        goodShips.add(new GameFieldShip(6, 10, 3, false));

        goodShips.forEach((goodShip) -> {
            assertFalse(goodShip.intersects(ship));
            assertFalse(ship.intersects(goodShip));
        });

        final ArrayList<GameFieldShip> badShips = new ArrayList<>();
        badShips.add(new GameFieldShip(10, 10, 3, true));
        badShips.add(new GameFieldShip(10, 10, 3, false));
        badShips.add(new GameFieldShip(10, 7, 3, true));
        badShips.add(new GameFieldShip(11, 8, 3, true));
        badShips.add(new GameFieldShip(6, 10, 4, false));
        badShips.add(new GameFieldShip(6, 11, 4, false));
        badShips.add(new GameFieldShip(12, 10, 4, true));
        badShips.add(new GameFieldShip(13, 9, 4, false));

        badShips.forEach((badShip) -> {
            assertTrue(badShip.intersects(ship));
            assertTrue(ship.intersects(badShip));
        });
    }

    @Test
    public void testShoot() throws GameFieldShipException {
        final GameFieldShip ship = new GameFieldShip(10, 10, 4, false);

        assertTrue(ship.shoot(10, 10));
        try {
            ship.shoot(10, 10);
            fail("Expected exception: already shot");
        }
        catch(GameFieldShipException e) {
            assertThat(e.getMessage(), StringContains.containsString("The deck is already shot"));
        }

        assertTrue(ship.shoot(11, 10));
        assertTrue(ship.shoot(12, 10));
        assertTrue(ship.shoot(13, 10));
        assertFalse(ship.shoot(14, 10));
        assertFalse(ship.shoot(13, 11));
    }

    @Test
    public void testIsValid() throws Exception {
        final GameFieldProperties props = GameFieldProperties.getProperties();
        if(props == null) return;

        final ArrayList<GameFieldShip> badShips = new ArrayList<>();
        badShips.add(new GameFieldShip(2, 0, 4, true));
        badShips.add(new GameFieldShip(8, 0, 4, false));
        badShips.add(new GameFieldShip(7, 8, 4, true));
        badShips.add(new GameFieldShip(8, 9, 4, true));
        badShips.add(new GameFieldShip(9, 10, 4, true));
        badShips.add(new GameFieldShip(10, 10, 2, true));
        badShips.add(new GameFieldShip(10, 10, 2, false));
        badShips.add(new GameFieldShip(10, 5, 4, false));
        badShips.add(new GameFieldShip(9, 6, 4, false));
        badShips.add(new GameFieldShip(8, 7, 4, false));

        badShips.forEach((badShip) -> assertFalse(badShip.isValidForGameFieldProperties(props)));

        final ArrayList<GameFieldShip> goodShips = new ArrayList<>();
        goodShips.add(new GameFieldShip(1, 1, 4, true));
        goodShips.add(new GameFieldShip(1, 1, 4, false));
        goodShips.add(new GameFieldShip(10, 10, 1, true));
        goodShips.add(new GameFieldShip(10, 10, 1, false));
        goodShips.add(new GameFieldShip(1, 9, 4, false));
        goodShips.add(new GameFieldShip(6, 7, 4, true));
        goodShips.add(new GameFieldShip(10, 5, 4, true));

        goodShips.forEach((goodShip) -> assertTrue(goodShip.isValidForGameFieldProperties(props)));
    }

    @Test
    public void testGetDecks() throws Exception {
        final GameFieldShip ship = new GameFieldShip(10, 10, 5, true);
        final ArrayList<GameFieldShipDeck> decks = ship.getDecks();

        assertEquals(decks.size(), 5);
        assertTrue(decks.contains(new GameFieldShipDeck(10, 10)));
        assertTrue(decks.contains(new GameFieldShipDeck(10, 11)));
        assertTrue(decks.contains(new GameFieldShipDeck(10, 12)));
        assertTrue(decks.contains(new GameFieldShipDeck(10, 13)));
        assertTrue(decks.contains(new GameFieldShipDeck(10, 14)));

        final GameFieldShip ship1 = new GameFieldShip(11, 10, 7, false);
        final ArrayList<GameFieldShipDeck> decks1 = ship1.getDecks();

        assertEquals(decks1.size(), 7);
        assertTrue(decks1.contains(new GameFieldShipDeck(11, 10)));
        assertTrue(decks1.contains(new GameFieldShipDeck(12, 10)));
        assertTrue(decks1.contains(new GameFieldShipDeck(13, 10)));
        assertTrue(decks1.contains(new GameFieldShipDeck(14, 10)));
        assertTrue(decks1.contains(new GameFieldShipDeck(15, 10)));
        assertTrue(decks1.contains(new GameFieldShipDeck(16, 10)));
        assertTrue(decks1.contains(new GameFieldShipDeck(17, 10)));
    }

    @Test
    public void testGetNearDecks() throws Exception {
        final GameFieldProperties props = GameFieldProperties.getProperties();
        if(props == null) return;

        final GameFieldShip ship = new GameFieldShip(10, 10, 1, true);
        final ArrayList<GameFieldShipDeck> decks = ship.getNearDecks(props);

        assertEquals(decks.size(), 3);
        assertTrue(decks.contains(new GameFieldShipDeck(9, 9)));
        assertTrue(decks.contains(new GameFieldShipDeck(10, 9)));
        assertTrue(decks.contains(new GameFieldShipDeck(9, 10)));

        final GameFieldShip ship1 = new GameFieldShip(10, 10, 1, false);
        final ArrayList<GameFieldShipDeck> decks1 = ship1.getNearDecks(props);

        assertEquals(decks1.size(), 3);
        assertTrue(decks1.contains(new GameFieldShipDeck(9, 9)));
        assertTrue(decks1.contains(new GameFieldShipDeck(10, 9)));
        assertTrue(decks1.contains(new GameFieldShipDeck(9, 10)));

        final GameFieldShip ship2 = new GameFieldShip(1, 1, 2, false);
        final ArrayList<GameFieldShipDeck> decks2 = ship2.getNearDecks(props);
        assertEquals(decks2.size(), 4);
        assertTrue(decks2.contains(new GameFieldShipDeck(1, 2)));
        assertTrue(decks2.contains(new GameFieldShipDeck(2, 2)));
        assertTrue(decks2.contains(new GameFieldShipDeck(3, 2)));
        assertTrue(decks2.contains(new GameFieldShipDeck(3, 1)));

        final GameFieldShip ship3 = new GameFieldShip(2, 2, 2, true);
        final ArrayList<GameFieldShipDeck> decks3 = ship3.getNearDecks(props);
        assertEquals(decks3.size(), 10);

        assertTrue(decks3.contains(new GameFieldShipDeck(1, 1)));
        assertTrue(decks3.contains(new GameFieldShipDeck(1, 2)));
        assertTrue(decks3.contains(new GameFieldShipDeck(1, 3)));
        assertTrue(decks3.contains(new GameFieldShipDeck(1, 4)));
        assertTrue(decks3.contains(new GameFieldShipDeck(3, 1)));
        assertTrue(decks3.contains(new GameFieldShipDeck(3, 2)));
        assertTrue(decks3.contains(new GameFieldShipDeck(3, 3)));
        assertTrue(decks3.contains(new GameFieldShipDeck(3, 4)));
        assertTrue(decks3.contains(new GameFieldShipDeck(2, 1)));
        assertTrue(decks3.contains(new GameFieldShipDeck(2, 4)));
    }
}