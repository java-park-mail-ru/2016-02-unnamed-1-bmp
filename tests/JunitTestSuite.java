import dbservice.DBServiceImpl;
import dbservice.UserServiceImplTest;
import frontend.servlets.SignInServletTest;
import frontend.servlets.SignUpServletTest;
import main.AccountServiceImplTest;

import main.ContextTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        SignInServletTest.class,
        SignUpServletTest.class,
        AccountServiceImplTest.class,
        UserServiceImplTest.class,
        ContextTest.class
})

public class JunitTestSuite {

}
