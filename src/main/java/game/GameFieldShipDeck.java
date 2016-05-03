package game;

import org.jetbrains.annotations.NotNull;

public class GameFieldShipDeck {

    private final int deckX;
    private final int deckY;

    public GameFieldShipDeck(int x, int y) {
        this.deckX = x;
        this.deckY = y;
    }

    public int getX() {
        return this.deckX;
    }

    public int getY() {
        return this.deckY;
    }

    public boolean isValidForGameFieldProperties(@NotNull GameFieldProperties properties) {
        boolean isValid = this.deckX > 0 && this.deckX <= properties.getSize();
        isValid = isValid && this.deckY > 0 && this.deckY <= properties.getSize();

        return isValid;
    }

    @Override
    public int hashCode() {
        return (this.deckX + this.deckY) * this.deckX + this.deckY;
    }

    @Override
    public boolean equals(Object deck) {
        return deck instanceof GameFieldShipDeck
                && ((GameFieldShipDeck) deck).deckX == this.deckX
                && ((GameFieldShipDeck) deck).deckY == this.deckY;
    }
}
