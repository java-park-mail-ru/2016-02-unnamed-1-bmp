import frontend.servlets.SignInServletTest;

import frontend.servlets.SignUpServletTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        SignInServletTest.class,
        SignUpServletTest.class
})

public class JunitTestSuite {

}
