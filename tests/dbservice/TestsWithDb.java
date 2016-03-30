package dbservice;

import base.DBService;
import base.UserService;
import com.sun.istack.internal.NotNull;
import main.LaunchException;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class TestsWithDb {
    @NotNull
    protected static DBService dbService;
    @NotNull
    protected static UserService userService;

    @BeforeClass
    public static void setUp() throws LaunchException {
        dbService = new DBServiceImpl(new Configuration().configure("h2config.xml"));
        userService = new UserServiceImpl(dbService);
    }

    @SuppressWarnings("JpaQlInspection")
    @After
    public void cleanUp() throws DatabaseException {
        dbService.doWork((session)->session.createQuery("DELETE from UserDataSet").executeUpdate());
    }

    @AfterClass
    public static void turnDown() {
        dbService.shutdown();
    }
}
