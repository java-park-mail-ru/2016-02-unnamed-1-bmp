package main;

import base.AccountService;
import org.junit.Test;

import static org.junit.Assert.*;


public class AccountServiceImplTest {

    public static final long USER_ID = 123L;

    @Test
    public void testAddSessions() throws Exception {
        final AccountService accountService = new AccountServiceImpl();

        accountService.addSessions("admin", USER_ID);
        assertEquals(USER_ID , accountService.getUserIdBySesssion("admin").longValue());
    }

    @Test
    public void testAddSessionsEqual() throws Exception {
        final AccountService accountService = new AccountServiceImpl();

        accountService.addSessions("admin", USER_ID);
        accountService.addSessions("admin", USER_ID+1);

        assertEquals(USER_ID , accountService.getUserIdBySesssion("admin").longValue());
    }


    @Test
    public void testLogout() throws Exception {
        final AccountService accountService = new AccountServiceImpl();

        accountService.addSessions("admin", USER_ID);

        assertTrue(accountService.logout("admin"));
        assertFalse(accountService.logout("admin"));
    }

    @Test
    public void testUserLoggedIn() throws Exception {
        final AccountService accountService = new AccountServiceImpl();

        accountService.addSessions("admin", USER_ID);

        assertTrue(accountService.userLoggedIn("admin"));
        accountService.logout("admin");
        assertFalse(accountService.userLoggedIn("admin"));
    }
}