package main;

import base.AccountService;
import org.junit.Test;

import static org.junit.Assert.*;


public class AccountServiceImplTest {

    @Test
    public void testAddSessions() throws Exception {
        final AccountService accountService = new AccountServiceImpl();

        accountService.addSessions("admin", 123L);
        assertEquals(123L , accountService.getUserIdBySesssion("admin").longValue());
    }

    @Test
    public void testAddSessionsEqual() throws Exception {
        final AccountService accountService = new AccountServiceImpl();

        accountService.addSessions("admin", 123L);
        accountService.addSessions("admin", 124L);

        assertEquals(123L , accountService.getUserIdBySesssion("admin").longValue());
    }


    @Test
    public void testLogout() throws Exception {
        final AccountService accountService = new AccountServiceImpl();

        accountService.addSessions("admin", 123L);

        assertTrue(accountService.logout("admin"));
        assertFalse(accountService.logout("admin"));
    }

    @Test
    public void testUserLoggedIn() throws Exception {
        final AccountService accountService = new AccountServiceImpl();

        accountService.addSessions("admin", 123L);

        assertTrue(accountService.userLoggedIn("admin"));
        accountService.logout("admin");
        assertFalse(accountService.userLoggedIn("admin"));
    }
}