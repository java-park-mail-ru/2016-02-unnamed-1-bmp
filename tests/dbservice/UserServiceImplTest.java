package dbservice;

import base.datasets.UserDataSet;
import org.junit.Test;

import static org.junit.Assert.*;


public class UserServiceImplTest extends TestsWithDb {

    public static final long HUGE_ID = 100000000L;

    @Test
    public void testSave() throws DatabaseException {
        final UserDataSet userDataSet = new UserDataSet("admin", "admin", "admin@admin.com");
        userService.saveUser(userDataSet);
        assertEquals(userService.getUserByEmail("admin@admin.com").getId(), userDataSet.getId());
    }

    @Test
    public void testSaveUserDoubleLogin() throws DatabaseException {
        final UserDataSet userDataSet = new UserDataSet("admin", "admin", "admin@admin.com");
        userService.saveUser(userDataSet);
        try {
            userService.saveUser(new UserDataSet("admin", "admin", "adminNew@admin.com"));
        } catch (DatabaseException e) {
            assertNotNull(e);
        }
        assertEquals(userService.getUserByEmail("admin@admin.com").getId(),userDataSet.getId());
    }

    @Test
    public void testSaveUserDoubleEmail() throws DatabaseException {
        final UserDataSet userDataSet = new UserDataSet("admin", "admin", "admin@admin.com");
        userService.saveUser(userDataSet);
        try {
            userService.saveUser(new UserDataSet("adminNew", "admin", "admin@admin.com"));
        } catch (DatabaseException e) {
            assertNotNull(e);
        }
        assertEquals(userService.getUserByEmail("admin@admin.com").getId(),userDataSet.getId());
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
        assertFalse(userService.deleteUserById(HUGE_ID));
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