package dbservice;

import base.DBService;
import base.UserService;
import base.datasets.UserDataSet;
import dbservice.dao.UserDataSetDAO;
import main.LaunchException;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.BDDMockito;


public class UserServiceImplTest extends TestsWithDb {
    @Test
    public void testSave() throws DatabaseException {
        userService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        assertEquals(userService.getUserByEmail("admin@admin.com").getLogin(), "admin");
        assertEquals(userService.getUserByLogin("admin").getEmail(), "admin@admin.com");
    }

    @Test
    public void testSaveUserDoubleLogin() throws DatabaseException {
        userService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        try {
            userService.saveUser(new UserDataSet("admin", "admin", "adminNew@admin.com"));
        } catch (DatabaseException e) {
            assertNotNull(e);
        }
        assertEquals(userService.getUserByEmail("admin@admin.com").getLogin(),"admin");
    }

    @Test
    public void testSaveUserDoubleEmail() throws DatabaseException {
        userService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        try {
            userService.saveUser(new UserDataSet("adminNew", "admin", "admin@admin.com"));
        } catch (DatabaseException e) {
            assertNotNull(e);
        }
        assertEquals(userService.getUserByEmail("admin@admin.com").getLogin(),"admin");
    }


    @Test
    public void testDeleteUserById() throws DatabaseException {
        userService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        final long addedId = userService.getUserByEmail("admin@admin.com").getId();
        assertTrue(userService.deleteUserById(addedId));
        assertTrue(userService.getUserById(addedId) == null);
    }

    @Test
    public void testDeleteUserByIdFail() throws DatabaseException {
        assertFalse(userService.deleteUserById(100000000L));
    }

    @Test
    public void testIsEmailInique() throws DatabaseException {
        userService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        assertFalse(userService.isEmailUnique("admin@admin.com"));
    }

    @Test
    public void testIsLoginInique() throws DatabaseException {
        userService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        assertFalse(userService.isLoginUnique("admin"));
    }

}