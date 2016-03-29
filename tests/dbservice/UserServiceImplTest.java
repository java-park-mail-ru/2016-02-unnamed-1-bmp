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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserServiceImplTest {
    UserService userService;
    DBService dbService;

    @Before
    public void toSet() throws LaunchException {
        dbService = new DBServiceImpl(new Configuration().configure("h2config.xml"));
        userService = new UserServiceImpl(dbService);
    }

    @After
    public void toReset() {
        dbService.shutdown();
    }

    @Test
    public void testSaveUser() throws DatabaseException {
        final UserDataSetDAO dao = mock(UserDataSetDAO.class);
        when(dao.save(any())).thenReturn(1L);
        assertTrue(userService.saveUser(new UserDataSet(anyString(), anyString(), anyString())) != -1);
    }

    @Test
    public void testSaveUserDoubleLogin() throws DatabaseException {
        final long newId = userService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        assertTrue(userService.saveUser(new UserDataSet("admin", "adminNew", "adminNew@admin.com")) == -1);
    }

    @Test
    public void testGetUserById() throws DatabaseException {
        final UserDataSet userDataSet = new UserDataSet("admin", "admin", "admin@admin.com");
        final long addedId = userService.saveUser(userDataSet);
        userDataSet.setId(addedId);
        final UserDataSet ret = userService.getUserById(addedId);
        assertEquals(ret.getLogin(), userDataSet.getLogin());
    }

    @Test
    public void testGetUserByEmail() throws DatabaseException {
        final UserDataSet userDataSet = new UserDataSet("admin", "admin", "admin@admin.com");
        final long addedId = userService.saveUser(userDataSet);
        assertEquals(userService.getUserByEmail("admin@admin.com").getId().longValue(), addedId);
    }

    @Test
    public void testGetUserByLogin() throws DatabaseException {
        final UserDataSet userDataSet = new UserDataSet("admin", "admin", "admin@admin.com");
        final long addedId = userService.saveUser(userDataSet);
        assertEquals(userService.getUserByLogin("admin").getId().longValue(), addedId);
    }

    @Test
    public void testUpdateUserInfo() throws DatabaseException {
        final UserDataSet userDataSet = new UserDataSet("admin", "admin", "admin@admin.com");
        final long addedId = userService.saveUser(userDataSet);
        assertTrue(userService.updateUserInfo(addedId, "admin", "adminNew", "adminNew@admin.com"));
        assertEquals("adminNew",userService.getUserById(addedId).getLogin());
    }

    @Test
    public void testDeleteUserById() throws DatabaseException {
        final long newId = userService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        assertTrue(userService.deleteUserById(newId));
        assertTrue(userService.getUserById(newId) == null);
    }

    @Test
    public void testDeleteUserByIdFail() throws DatabaseException {
        assertFalse(userService.deleteUserById(100000L));
    }
}