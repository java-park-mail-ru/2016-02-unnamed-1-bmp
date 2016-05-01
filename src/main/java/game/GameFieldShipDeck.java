package game;

public class GameFieldShipDeck {

    private int deckX;
    private int deckY;

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
