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
        isValid = isValid && (this.ships.stream().allMatch((curShip) -> !curShip.intersects(ship)));

        return isValid;
    }

    public long countShips(int length) {
        return this.ships.stream().filter((ship) -> ship.getLength() == length).count();
    }

    public int countShips() {
        return this.ships.size();
    }

    public GameFieldShootResult shoot(int x, int y) {
        for (GameFieldShip ship : this.ships) {
            try {
                if (ship.shoot(x, y)) {
                    final GameFieldShootResult.GameFieldShootState state = ship.isKilled() ?
                            GameFieldShootResult.GameFieldShootState.STATE_KILLED :
                            GameFieldShootResult.GameFieldShootState.STATE_WOUND;
                    return new GameFieldShootResult(x, y, state, ship);
                }
            } catch (GameFieldShipException e) {
                return new GameFieldShootResult(x, y, GameFieldShootResult.GameFieldShootState.STATE_ALREADY, ship);
            }
        }
        return new GameFieldShootResult(x, y, GameFieldShootResult.GameFieldShootState.STATE_MISS);
    }

    public boolean isKilled() {
        return this.ships.stream().filter((ship) -> !ship.isKilled()).count() == 0;
    }

    public boolean isValid() {
        for (int i = 1; i <= this.gameFieldProperties.getMaxDeck(); i++) {
            if (this.gameFieldProperties.getShips(i) != this.countShips(i)) {
                return false;
            }
        }
        return true;
    }
}