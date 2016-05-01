package game;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GameFieldShip {

    private int startX;
    private int startY;
    private int length;
    private boolean isVertical;
    private ArrayList<Boolean> decks = new ArrayList<>();

    public GameFieldShip(int x, int y, int length, boolean isVertical) {
        this.startX = x;
        this.startY = y;
        this.length = length;
        this.isVertical = isVertical;

        for(int i = 0; i < length; i++) {
            this.decks.add(true);
        }
    }

    public int getLength() {
        return this.length;
    }

    public boolean isKilled() {
        return !this.decks.contains(true);
    }

    public boolean isFull() {
        return !this.decks.contains(false);
    }

    public boolean containsDeck(int x, int y) {
        if(isVertical) {
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

        for(int i = 0; i < ship.length; i++) {
            final int curX = ship.isVertical ? ship.startX : ship.startX + i;
            final int curY = ship.isVertical ? ship.startY + i : ship.startY;

            final boolean betweenX = minX <= curX && maxX >= curX;
            final boolean betweenY = minY <= curY && maxY >= curY;

            if(betweenX && betweenY) {
                return true;
            }
        }

        return false;
    }

    public boolean shoot(int x, int y) throws GameFieldShipException {
        if(this.containsDeck(x, y)) {
            final int key = (this.isVertical ? y - this.startY : x - this.startX);
            if(!this.decks.get(key)) {
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
}
