import frontend.servlets.SignInServletTest;
import frontend.servlets.SignUpServletTest;
import main.AccountServiceImplTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        SignInServletTest.class,
        SignUpServletTest.class,
        AccountServiceImplTest.class
})

public class JunitTestSuite {

}
