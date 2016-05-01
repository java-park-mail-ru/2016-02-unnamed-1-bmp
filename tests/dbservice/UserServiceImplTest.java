package dbservice;

import base.datasets.UserDataSet;
import org.junit.Test;

import static org.junit.Assert.*;


public class UserServiceImplTest extends TestsWithDb {

    public static final long HUGE_ID = 100000000L;

    @Test
    public void testSave() throws DatabaseException {
        final UserDataSet userDataSet = new UserDataSet("admin", "admin", "admin@admin.com");
        final long id = userService.saveUser(userDataSet);
        assertEquals(userService.getUserById(id).getId(), userDataSet.getId());
        assertFalse(userService.getUserById(id).getIsAnonymous());
    }

    @Test
    public void testSaveAnonymous() throws DatabaseException {
        final UserDataSet userDataSet = new UserDataSet("admin");
        final Long id = userService.saveUser(userDataSet);
        assertTrue(userService.getUserById(id).getIsAnonymous());
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
    public void testSaveUserAnonymousDoubleLogin() throws DatabaseException {
        final Long id1 = userService.saveUser(new UserDataSet("admin"));
        final Long id2 = userService.saveUser(new UserDataSet("admin"));

        assertNotEquals(userService.getUserById(id1).getId(), userService.getUserById(id2).getId());
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

    @Test
    public void testIsLoginIniqueAnonymous() throws DatabaseException {
        userService.saveUser(new UserDataSet("admin"));
        userService.saveUser(new UserDataSet("admin"));
        assertTrue(userService.isLoginUnique("admin"));
    }

    @Test
    public void testGetByLoginAnonymous() throws DatabaseException {
        userService.saveUser(new UserDataSet("random"));
        final UserDataSet user = userService.getUserByLogin("random");
        assertNull(user);
    }

}