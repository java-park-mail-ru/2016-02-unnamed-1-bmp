package game;

import java.util.ArrayList;

public class GameField {
    private GameFieldProperties gameFieldProperties;
    private ArrayList<GameFieldShip> ships = new ArrayList<>();

    public GameField(GameFieldProperties gameFieldProperties) {
        this.gameFieldProperties = gameFieldProperties;
    }

    public GameFieldProperties getProperties() {
        return this.gameFieldProperties;
    }

    public boolean addShip(GameFieldShip ship) {
        return this.isValidShip(ship) && this.ships.add(ship);
    }

    public boolean isValidShip(GameFieldShip ship) {
        boolean isValid = ship.isValidForGameFieldProperties(this.gameFieldProperties);
        isValid = isValid && this.countShips(ship.getLength()) < this.gameFieldProperties.getShips(ship.getLength());
        isValid = isValid && !(this.ships.stream().allMatch((curShip) -> !curShip.intersects(ship)));

        return isValid;
    }

    public long countShips(int length) {
        return this.ships.stream().filter((ship) -> ship.getLength() == length).count();
    }

    public int countShips() {
        return this.ships.size();
    }

    public boolean shoot(int x, int y) { // TODO: shoot result
        try {
            for(GameFieldShip ship : this.ships) {
                if(ship.shoot(x, y)) {
                    return true;
                }
            }
        }
        catch(GameFieldShipException e) {
            return false;
        }
        return false;
    }

    public boolean isKilled() {
        return this.ships.stream().filter((ship) -> !ship.isKilled()).count() == 0;
    }

    public boolean isValid() {
        for(int i = 1; i <= this.gameFieldProperties.getMaxDeck(); i++) {
            if(this.gameFieldProperties.getShips(i) != this.countShips(i)) {
                return false;
            }
        }
        return true;
    }
}