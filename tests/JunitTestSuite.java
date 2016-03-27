import dbservice.DBServiceImpl;
//import frontend.servlets.SignInServletTest;
//import frontend.servlets.SignUpServletTest;
import main.AccountServiceImplTest;
//import dbservice.DBServiceImplTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
//        SignInServletTest.class,
//        SignUpServletTest.class,
        AccountServiceImplTest.class,
//        DBServiceImplTest.class
})

public class JunitTestSuite {

}
