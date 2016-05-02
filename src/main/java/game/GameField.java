package game;

import java.util.ArrayList;
import java.util.HashMap;

public class GameField {
    private GameFieldProperties gameFieldProperties;
    private ArrayList<GameFieldShip> ships = new ArrayList<>();
    private ArrayList<GameFieldShootResult> shoots = new ArrayList<>();

    public GameField(GameFieldProperties gameFieldProperties) {
        this.gameFieldProperties = gameFieldProperties;
    }

    public GameFieldProperties getProperties() {
        return this.gameFieldProperties;
    }

    public ArrayList<GameFieldShip> getShips() {
        return this.ships;
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
        GameFieldShootResult result;
        for (GameFieldShip ship : this.ships) {
            try {
                if (ship.shoot(x, y)) {
                    final GameFieldShootResult.GameFieldShootState state = ship.isKilled() ?
                            GameFieldShootResult.GameFieldShootState.STATE_KILLED :
                            GameFieldShootResult.GameFieldShootState.STATE_WOUND;
                    result = new GameFieldShootResult(x, y, state, ship);
                    this.shoots.add(result);
                    return result;
                }
            } catch (GameFieldShipException e) {
                result = new GameFieldShootResult(x, y, GameFieldShootResult.GameFieldShootState.STATE_ALREADY, ship);
                this.shoots.add(result);
                return result;
            }
        }
        result = new GameFieldShootResult(x, y, GameFieldShootResult.GameFieldShootState.STATE_MISS);
        this.shoots.add(result);
        return result;
    }

    public ArrayList<GameFieldShootResult> getShoots() {
        return this.shoots;
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

    public static GameField generateRandomField(GameFieldProperties gameFieldProperties) {
        final int maxDeck = gameFieldProperties.getMaxDeck();
        final int size = gameFieldProperties.getSize();

        final GameField field = new GameField(gameFieldProperties);
        final ArrayList<GameFieldShipDeck> busyCells = new ArrayList<>();
        for(int decks = maxDeck; decks > 0; decks--) {
            final int maxShips = gameFieldProperties.getShips(decks);
            for(int ships = 0; ships < maxShips; ships++) {
                while(true) {
                    final int x = (int) Math.floor(Math.random() * size) + 1;
                    final int y = (int) Math.floor(Math.random() * size) + 1;

                    if(busyCells.contains(new GameFieldShipDeck(x, y))) {
                        continue;
                    }
                    final boolean vertical = Math.random() > 0.49;

                    GameFieldShip ship;
                    if(field.addShip(ship = new GameFieldShip(x, y, decks, vertical))) {
                        ship.getDecks().forEach(busyCells::add);
                        ship.getNearDecks(gameFieldProperties).forEach(busyCells::add);
                        break;
                    }
                    if(field.addShip(ship = new GameFieldShip(x, y, decks, !vertical))) {
                        ship.getDecks().forEach(busyCells::add);
                        ship.getNearDecks(gameFieldProperties).forEach(busyCells::add);
                        break;
                    }
                }
            }
        }

        return field;

    }
}