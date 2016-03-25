package dbservice;
import base.DBService;
import base.datasets.UserDataSet;
import dbservice.dao.UserDataSetDAO;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.spi.ServiceException;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DBServiceImplTest {
    DBService dbService;

    @Before
    public void toSet() {
        dbService = new DBServiceImpl(new Configuration().configure("h2config.xml"));
    }

    @After
    public void toReset() {
        dbService.shutdown();
    }

    @Test(expected=ServiceException.class)
    public void testDBServiceImpl() throws IOException, ServiceException {
        final Configuration configuration = new Configuration();
        final DBService dbServiceImp = new DBServiceImpl(configuration);
        dbServiceImp.shutdown();
    }

    @Test
    public void testSaveUser() {
        final UserDataSetDAO dao = mock(UserDataSetDAO.class);
        when(dao.save(any())).thenReturn(1L);
        assertTrue(dbService.saveUser(new UserDataSet(anyString(), anyString(), anyString())) != -1);
    }

    @Test
    public void testSaveUserDoubleLogin() {
        final long newId = dbService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        assertTrue(dbService.saveUser(new UserDataSet("admin", "adminNew", "adminNew@admin.com")) == -1);
    }

    @Test
    public void testGetUserById() {
        final UserDataSet userDataSet = new UserDataSet("admin", "admin", "admin@admin.com");
        final long addedId = dbService.saveUser(userDataSet);
        userDataSet.setId(addedId);
        final UserDataSet ret = dbService.getUserById(addedId);
        assertEquals(ret.getLogin(), userDataSet.getLogin());
    }

    @Test
    public void testGetUserByLogin() {
        final UserDataSet userDataSet = new UserDataSet("admin", "admin", "admin@admin.com");
        final long addedId = dbService.saveUser(userDataSet);
        assertEquals(dbService.getUserByLogin("admin").getId().longValue(), addedId);
    }


    @Test
    public void testUpdateUserInfo() {
        final long newId = dbService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        assertTrue(dbService.updateUserInfo(newId, "new@email", "admin", "admin"));
    }

    @Test
    public void testDeleteUserById() {
        final long newId = dbService.saveUser(new UserDataSet("admin", "admin", "admin@admin.com"));
        assertTrue(dbService.deleteUserById(newId));
    }

    @Test
    public void testDeleteUserByIdFail() {
        assertFalse(dbService.deleteUserById(100000L));
    }

}