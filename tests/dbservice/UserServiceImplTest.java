package dbservice;

import base.DBService;
import base.UserService;
import base.datasets.UserDataSet;
import dbservice.dao.UserDataSetDAO;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import dbservice.DBServiceImpl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserServiceImplTest {
    UserService userService;

    @Before
    public void toSet() {
        final DBService dbService = new DBServiceImpl(new Configuration().configure("h2config.xml"));
        userService = new UserServiceImpl(dbService);
    }


    @Test
    public void testSaveUser() throws DatabaseException {
        final long newId = userService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        assertTrue(userService.saveUser(new UserDataSet("admin", "adminNew", "adminNew@admin.com")) == -1);
    }

    @Test
    public void testGetUserById() throws Exception {

    }

    @Test
    public void testGetUserByEmail() throws Exception {

    }

    @Test
    public void testGetUserByLogin() throws Exception {

    }

    @Test
    public void testUpdateUserInfo() throws Exception {

    }

    @Test
    public void testDeleteUserById() throws Exception {

    }
}