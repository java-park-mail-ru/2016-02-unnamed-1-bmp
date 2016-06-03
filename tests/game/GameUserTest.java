package game;

import base.DBService;
import base.UserService;
import base.datasets.UserDataSet;
import dbservice.DatabaseException;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class GameUserTest {

    @Test
    public void testGetName() throws Exception {
        final GameField gameField = mock(GameField.class);
        final String name = "Le me";

        final GameUser gameUser = new GameUser(name, gameField);
        assertEquals(gameUser.getName(), name);

        final UserDataSet user = mock(UserDataSet.class);
        when(user.getLogin()).thenReturn(name);

        final UserService userService = mock(UserService.class);

        final GameUser gameUser1 = new GameUser(user, gameField, userService);

        assertEquals(gameUser1.getName(), name);
    }

    @Test
    public void testGetScore() throws Exception {
        final GameField gameField = mock(GameField.class);
        final GameUser gameUser = new GameUser("Le me", gameField);

        assertEquals(gameUser.getScore(), 0);

        final Integer score = 100;
        final UserDataSet user = mock(UserDataSet.class);
        when(user.getScore()).thenReturn(score);

        final UserService userService = mock(UserService.class);

        final GameUser gameUser1 = new GameUser(user, gameField, userService);

        assertEquals(Long.valueOf(gameUser1.getScore()), Long.valueOf(score));
    }

    @Test
    public void testOffline() throws InterruptedException {
        final GameField gameField = mock(GameField.class);
        final GameUser gameUser = new GameUser("Le me", gameField);

        assertEquals(gameUser.getOfflineDuration(), Long.valueOf(0));

        gameUser.setOffline();
        Thread.sleep(1000);
        assertNotEquals(gameUser.getOfflineDuration(), Long.valueOf(0));

        gameUser.setOnline();
        assertEquals(gameUser.getOfflineDuration(), Long.valueOf(0));
    }

    @Test
    public void testIncrementScore() throws DatabaseException {
        final GameField gameField = mock(GameField.class);
        final UserDataSet user = mock(UserDataSet.class);
        when(user.getId()).thenReturn(1L);
        when(user.getScore()).thenReturn(100);

        final UserDataSet user1 = mock(UserDataSet.class);
        when(user1.getId()).thenReturn(1L);
        when(user1.getScore()).thenReturn(101);

        final UserService userService = mock(UserService.class);
        doNothing().when(userService).incrementUserScore(1L);
        when(userService.getUserById(1L)).thenReturn(user1);

        final GameUser gameUser = new GameUser(user, gameField, userService);
        assertEquals(Long.valueOf(gameUser.getScore()), Long.valueOf(100));
        gameUser.incScore();
        assertEquals(Long.valueOf(gameUser.getScore()), Long.valueOf(101));
        assertEquals(gameUser.getUser(), user1);
    }
}