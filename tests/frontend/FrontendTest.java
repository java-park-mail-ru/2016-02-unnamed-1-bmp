package frontend;

import base.AccountService;
import base.UserService;
import com.sun.istack.internal.NotNull;
import dbservice.UserServiceImpl;
import main.AccountServiceImpl;
import main.Context;
import org.junit.BeforeClass;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FrontendTest {
    @NotNull
    public static UserService userService;
    @NotNull
    public static AccountService accountService;
    @NotNull
    public static Context context;


    @BeforeClass
    public static void setUp() {
        accountService = mock(AccountServiceImpl.class);
        userService = mock(UserServiceImpl.class);

        context  = new Context();
        context.add(AccountService.class, accountService);
        context.add(UserService.class, userService);
    }

    protected HttpServletResponse getMockedResponse(StringWriter stringWriter) throws IOException {
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        return response;
    }

    protected HttpServletRequest getMockedRequest() {
        final HttpSession httpSession = mock(HttpSession.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(httpSession);
        return request;
    }

}
