package game;

public class GameFieldShootResult {

    public enum GameFieldShootState {
        STATE_MISS,
        STATE_ALREADY,
        STATE_WOUND,
        STATE_KILLED
    };

    private GameFieldShootState state;
    private GameFieldShip ship;

    GameFieldShootResult() {
        this.state = GameFieldShootState.STATE_MISS;
        this.ship = null;
    }

    GameFieldShootResult(GameFieldShootState state) {
        this.state = state;
        this.ship = null;
    }

    GameFieldShootResult(GameFieldShootState state, GameFieldShip ship) {
        this.state = state;
        this.ship = ship;
    }

    public GameFieldShip getShip() {
        return this.ship;
    }

    public GameFieldShootState getState() {
        return this.state;
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

}
