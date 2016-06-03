package game;

import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class GameFieldShipDeckTest {

    @Test
    public void testIsValid() throws Exception {
        final GameFieldProperties props = GameFieldProperties.getProperties();

        if(props == null) return;

        final Integer max = props.getSize();

        final GameFieldShipDeck deck = new GameFieldShipDeck(max + 1, max);
        assertFalse(deck.isValidForGameFieldProperties(props));

        final GameFieldShipDeck deck1 = new GameFieldShipDeck(max, max + 1);
        assertFalse(deck1.isValidForGameFieldProperties(props));

        final GameFieldShipDeck deck2 = new GameFieldShipDeck(max, max);
        assertTrue(deck2.isValidForGameFieldProperties(props));

        final GameFieldShipDeck deck3 = new GameFieldShipDeck(0, max);
        assertFalse(deck3.isValidForGameFieldProperties(props));

        final GameFieldShipDeck deck4 = new GameFieldShipDeck(max, 0);
        assertFalse(deck4.isValidForGameFieldProperties(props));

        final GameFieldShipDeck deck5 = new GameFieldShipDeck(1, 1);
        assertTrue(deck5.isValidForGameFieldProperties(props));

        final GameFieldShipDeck deck6 = new GameFieldShipDeck(max - 1, max - 1);
        assertTrue(deck6.isValidForGameFieldProperties(props));
    }

    @Test
    public void testEquals() throws Exception {
        final GameFieldShipDeck deck = new GameFieldShipDeck(1, 1);
        final GameFieldShipDeck deck1 = new GameFieldShipDeck(1, 1);

        assertEquals(deck, deck1);
    }
}