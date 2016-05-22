package game;

import com.sun.istack.internal.NotNull;

import java.util.*;

public class GameFieldShip {

    private final int startX;
    private final int startY;
    private final int length;
    private final boolean isVertical;
    private final ArrayList<Boolean> decks = new ArrayList<>();

    public GameFieldShip(int x, int y, int length, boolean isVertical) {
        this.startX = x;
        this.startY = y;
        this.length = length;
        this.isVertical = isVertical;

        for (int i = 0; i < length; i++) {
            this.decks.add(true);
        }
    }

    public int getLength() {
        return this.length;
    }

    public int getX() {
        return this.startX;
    }

    public int getY() {
        return this.startY;
    }

    public boolean isVertical() {
        return this.isVertical;
    }

    public boolean isKilled() {
        return !this.decks.contains(true);
    }

    public boolean containsDeck(int x, int y) {
        if (isVertical) {
            return this.startX == x && y >= this.startY && y < this.startY + this.length;
        }
        return this.startY == y && x >= this.startX && x < this.startX + this.length;
    }

    public boolean intersects(GameFieldShip ship) {
        final int minX = this.startX - 1;
        final int minY = this.startY - 1;
        final int lengthX = isVertical ? 3 : this.length + 2;
        final int lengthY = isVertical ? this.length + 2 : 3;
        final int maxX = minX + lengthX - 1;
        final int maxY = minY + lengthY - 1;

        for (int i = 0; i < ship.length; i++) {
            final int curX = ship.isVertical ? ship.startX : ship.startX + i;
            final int curY = ship.isVertical ? ship.startY + i : ship.startY;

            final boolean betweenX = minX <= curX && maxX >= curX;
            final boolean betweenY = minY <= curY && maxY >= curY;

            if (betweenX && betweenY) {
                return true;
            }
        }

        return false;
    }

    public boolean shoot(int x, int y) throws GameFieldShipException {
        if (this.containsDeck(x, y)) {
            final int key = (this.isVertical ? y - this.startY : x - this.startX);
            if (!this.decks.get(key)) {
                throw new GameFieldShipException("The deck is already shot");
            }
            this.decks.set(key, false);
            return true;
        }
        return false;
    }

    public boolean isValidForGameFieldProperties(@NotNull GameFieldProperties properties) {
        boolean isValid = properties.getMaxDeck() >= this.decks.size();

        isValid = isValid && this.startX > 0 && this.startX <= properties.getSize();
        isValid = isValid && this.startY > 0 && this.startY <= properties.getSize();

        isValid = isValid && (isVertical ? this.startY : this.startX) + this.length - 1 <= properties.getSize();

        return isValid;
    }

    public ArrayList<GameFieldShipDeck> getDecks() {
        final ArrayList<GameFieldShipDeck> result = new ArrayList<>();
        for (int i = 0; i < this.length; i++) {
            final int curX = this.isVertical ? this.startX : this.startX + i;
            final int curY = this.isVertical ? this.startY + i : this.startY;
            result.add(new GameFieldShipDeck(curX, curY));
        }
        return result;
    }

    @SuppressWarnings("OverlyComplexMethod")
    public ArrayList<GameFieldShipDeck> getNearDecks(GameFieldProperties properties) {
        final ArrayList<GameFieldShipDeck> result = new ArrayList<>();

        if (this.isVertical) {
            for (int y = this.startY - 1; y <= this.startY + this.length; y++) {
                if (y > 0 && y <= properties.getSize()) {
                    if (this.startX > 1) {
                        result.add(new GameFieldShipDeck(this.startX - 1, y));
                    }
                    if (this.startX < properties.getSize()) {
                        result.add(new GameFieldShipDeck(this.startX + 1, y));
                    }
                    if (y == this.startY - 1 || y == this.startY + this.length) {
                        result.add(new GameFieldShipDeck(this.startX, y));
                    }
                }
            }
        } else {
            for (int x = this.startX - 1; x <= this.startX + this.length; x++) {
                if (x > 0 && x <= properties.getSize()) {
                    if (this.startY > 1) {
                        result.add(new GameFieldShipDeck(x, this.startY - 1));
                    }
                    if (this.startY < properties.getSize()) {
                        result.add(new GameFieldShipDeck(x, this.startY + 1));
                    }
                    if (x == this.startX - 1 || x == this.startX + this.length) {
                        result.add(new GameFieldShipDeck(x, this.startY));
                    }
                }
            }
        }
        return result;
    }
}
