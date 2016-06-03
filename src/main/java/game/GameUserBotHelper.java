package game;

import java.util.*;

public class GameUserBotHelper {

    private final GameUser gameUser;
    private final GameSession gameSession;

    private final HashMap<GameFieldShipDeck, GameFieldShootResult> shoots = new HashMap<>();
    private final ArrayList<GameFieldShipDeck> latestShoots = new ArrayList<>();

    public GameUserBotHelper(GameSession gameSession, GameUser gameUser) {
        this.gameSession = gameSession;
        this.gameUser = gameUser;
    }

    public void shoot() {
        if (!this.gameSession.isStarted()) {
            return;
        }

        final GameFieldShipDeck deck = this.getNextShoot();

        this.gameSession.shoot(this.gameUser, deck.getX(), deck.getY(), (result) -> {
            if (result == null) {
                return;
            }

            this.shoots.put(deck, result);

            if (result.isKilled()) {
                final GameFieldShip ship = result.getShip();
                if (ship != null) {
                    ship.getNearDecks(this.gameSession.getGameFieldProperties()).stream().forEach(nearDeck -> {
                        final GameFieldShootResult deckRes = new GameFieldShootResult(nearDeck.getX(), nearDeck.getY(),
                                GameFieldShootResult.GameFieldShootState.STATE_MISS);
                        this.shoots.put(nearDeck, deckRes);
                    });
                }
                this.latestShoots.clear();
            } else if (result.isWound()) {
                this.latestShoots.add(deck);
            }
        });
    }

    @SuppressWarnings("OverlyComplexMethod")
    public GameFieldShipDeck getNextShoot() {
        GameFieldShipDeck deck;

        final int size = this.gameSession.getGameFieldProperties().getSize();

        if (this.latestShoots.isEmpty()) {
            while (true) {
                deck = new GameFieldShipDeck((int) Math.floor(Math.random() * size) + 1, (int) Math.floor(Math.random() * size) + 1);
                if (this.isValidShoot(deck)) {
                    break;
                }
            }
        } else {
            final ArrayList<GameFieldShipDeck> availableShoots = new ArrayList<>();
            if (this.latestShoots.size() == 1) {
                final GameFieldShipDeck latestShoot = this.latestShoots.get(0);
                availableShoots.add(new GameFieldShipDeck(latestShoot.getX(), latestShoot.getY() - 1));
                availableShoots.add(new GameFieldShipDeck(latestShoot.getX(), latestShoot.getY() + 1));
                availableShoots.add(new GameFieldShipDeck(latestShoot.getX() - 1, latestShoot.getY()));
                availableShoots.add(new GameFieldShipDeck(latestShoot.getX() + 1, latestShoot.getY()));
            } else {
                final boolean vertical = this.latestShoots.stream().allMatch(shoot -> shoot.getX() == this.latestShoots.get(0).getX());
                int min = size;
                int max = 0;
                for (GameFieldShipDeck shoot : this.latestShoots) {
                    if (vertical) {
                        if (shoot.getY() < min) {
                            min = shoot.getY();
                        }
                        if (shoot.getY() > max) {
                            max = shoot.getY();
                        }
                    } else {
                        if (shoot.getX() < min) {
                            min = shoot.getX();
                        }
                        if (shoot.getX() > max) {
                            max = shoot.getX();
                        }
                    }
                }
                final int constVar = vertical ? this.latestShoots.get(0).getX() : this.latestShoots.get(0).getY();
                availableShoots.add(new GameFieldShipDeck(vertical ? constVar : min - 1, vertical ? min - 1 : constVar));
                availableShoots.add(new GameFieldShipDeck(vertical ? constVar : max + 1, vertical ? max + 1 : constVar));
            }
            Collections.shuffle(availableShoots, new Random(new Date().getTime()));
            deck = availableShoots.stream().filter(this::isValidShoot).findFirst().get();
        }

        return deck;
    }

    public boolean isValidShoot(GameFieldShipDeck deck) {
        return this.shoots.get(deck) == null && deck.isValidForGameFieldProperties(this.gameSession.getGameFieldProperties());
    }

}
