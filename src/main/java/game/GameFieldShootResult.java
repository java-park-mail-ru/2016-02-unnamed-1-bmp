package game;

public class GameFieldShootResult {

    private final GameFieldShootState state;
    private final GameFieldShip ship;
    private final int shootX;
    private final int shootY;
    GameFieldShootResult(int x, int y, GameFieldShootState state) {
        this.shootX = x;
        this.shootY = y;
        this.state = state;
        this.ship = null;
    }

    GameFieldShootResult(int x, int y, GameFieldShootState state, GameFieldShip ship) {
        this.shootX = x;
        this.shootY = y;
        this.state = state;
        this.ship = ship;
    }

    public GameFieldShip getShip() {
        return this.ship;
    }

    public GameFieldShootState getState() {
        return this.state;
    }

    public int getX() {
        return this.shootX;
    }

    public int getY() {
        return this.shootY;
    }

    public boolean isMiss() {
        return this.state == GameFieldShootState.STATE_MISS;
    }

    public boolean isAlready() {
        return this.state == GameFieldShootState.STATE_ALREADY;
    }

    public boolean isWound() {
        return this.state == GameFieldShootState.STATE_WOUND;
    }

    public boolean isKilled() {
        return this.state == GameFieldShootState.STATE_KILLED;
    }

    public enum GameFieldShootState {
        STATE_MISS,
        STATE_ALREADY,
        STATE_WOUND,
        STATE_KILLED
    }

}
