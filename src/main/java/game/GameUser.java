package game;

import base.UserService;
import base.datasets.UserDataSet;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import dbservice.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class GameUser {
    private static final Logger LOGGER = LogManager.getLogger(GameUser.class);
    private final String name;
    private final UserDataSet user;

    private final UserService userService;

    private final boolean isBot;
    private final GameField field;
    private GameUserBotHelper botHelper;
    private Long offlineTime = null;

    public GameUser(String name, GameField field) {
        this.name = name;
        this.user = null;
        this.userService = null;
        this.field = field;
        this.isBot = true;
    }

    public GameUser(UserDataSet user, GameField field, UserService userService) {
        this.name = null;
        this.user = user;
        this.userService = userService;
        this.field = field;
        this.isBot = false;
        this.botHelper = null;
    }

    @NotNull
    public String getName() {
        if (this.user != null && this.user.getLogin() != null) {
            return this.user.getLogin();
        }

        if (this.name != null) {
            return this.name;
        }
        return "";
    }

    @Nullable
    public UserDataSet getUser() {
        return this.user;
    }

    @Nullable
    public GameUserBotHelper getBotHelper() {
        return this.botHelper;
    }

    public void setBotHelper(@NotNull GameUserBotHelper botHelper) {
        this.botHelper = botHelper;
    }

    public void incScore() {
        try {
            if (this.user != null && this.userService != null) {
                this.userService.incrementUserScore(this.user.getId());
            }
        } catch (DatabaseException e) {
            LOGGER.info("Raise database exceplion", e);
        }
    }

    public int getScore() {
        if (this.user != null) {
            return this.user.getScore();
        }

        return 0;
    }

    public boolean isReadyForGame() {
        return this.field.isValid();
    }

    public boolean isBot() {
        return this.isBot;
    }

    public GameField getField() {
        return this.field;
    }

    public GameFieldProperties getFieldProperties() {
        return this.field.getProperties();
    }

    public void setOnline() {
        this.offlineTime = null;
    }

    public void setOffline() {
        this.offlineTime = new Date().getTime();
    }

    public Long getOfflineDuration() {
        if (this.offlineTime == null) {
            return 0L;
        }

        return new Date().getTime() - this.offlineTime;
    }
}