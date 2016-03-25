package frontend.servlets;

import base.datasets.UserDataSet;
import org.hamcrest.core.StringContains;
import base.AccountService;
import base.DBService;
import dbservice.DBServiceImpl;
import main.AccountServiceImpl;
import main.Context;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.InputStreamReader;
import org.apache.commons.io.IOUtils;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

public class SignInServletTest {
    private AccountService accountService;
    private Context context;
    private DBService dbService;

    private HttpServletResponse getMockedResponse(StringWriter stringWriter) throws IOException {
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        return response;
    }

    private HttpServletRequest getMockedRequest() {
        final HttpSession httpSession = mock(HttpSession.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getSession()).thenReturn(httpSession);

        return request;
    }

    @Before
    public void setUp() {
        accountService = mock(AccountServiceImpl.class);
        dbService = mock(DBServiceImpl.class);

        context  = new Context();
        context.add(AccountService.class, accountService);
        context.add(DBService.class, dbService);
    }

    @Test
    public void testDoGetOk() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        when(request.getSession().getId()).thenReturn("1");
        when(accountService.getUserIdBySesssion("1")).thenReturn(1L);

        final SignInServlet signInServlet = new SignInServlet(context);

        signInServlet.doGet(request, response);

        assertThat(stringWriter.toString(),StringContains.containsString("{\"id\":1}"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDoGetUnauthUser() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        when(request.getSession().getId()).thenReturn("1");
        when(accountService.getUserIdBySesssion("1")).thenReturn(null);

        final SignInServlet signInServlet = new SignInServlet(context);

        signInServlet.doGet(request, response);
        assertThat(stringWriter.toString(),StringContains.containsString("{\"error\":\"User not authorized\""));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }


    @Test
    public void testDoPost() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        final String input = "{\"login\":\"admin\",\"password\":\"admin\"}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));
        final UserDataSet newUser = mock(UserDataSet.class);

        when(request.getReader()).thenReturn(bufferedReader);
        when(dbService.getUserByLogin("admin")).thenReturn(newUser);
        when(newUser.getPassword()).thenReturn("admin");
        when(newUser.getId()).thenReturn(1L);

        final SignInServlet signInServlet = new SignInServlet(context);
        signInServlet.doPost(request, response);

        final String str = "{\"id\":" + newUser.getId();

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(stringWriter.toString(),StringContains.containsString(str));
    }

    @Test
    public void testDoPostNotAllParams() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        final String input = "{\"login\":\"admin\"}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));

        when(request.getReader()).thenReturn(bufferedReader);

        final SignInServlet signInServlet = new SignInServlet(context);
        signInServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(stringWriter.toString(),StringContains.containsString("{\"error\":\"Not all params send\"}"));
    }


    @Test
    public void testDoPostWrongParams() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        final String input = "{\"login\":\"admin\",\"password\":\"admin\"}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));

        when(request.getReader()).thenReturn(bufferedReader);
        when(dbService.getUserByLogin("admin")).thenReturn(null);

        final SignInServlet signInServlet = new SignInServlet(context);
        signInServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(stringWriter.toString(),StringContains.containsString("{\"error\":\"Wrong email or password\"}"));
    }

    @Test
    public void testDoPostCantParseJson() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        final String input = "{login\":\"admin\",\"password\":\"admin}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));

        when(request.getReader()).thenReturn(bufferedReader);
        final SignInServlet signInServlet = new SignInServlet(context);
        signInServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(stringWriter.toString(),StringContains.containsString("{\"error\":\"Wrong json\"}"));
    }

    @Test
    public void testDoDelete() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        final String sessionId = request.getSession().getId();
        when(accountService.logout(sessionId)).thenReturn(true);

        final SignInServlet signInServlet = new SignInServlet(context);
        signInServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(stringWriter.toString(),StringContains.containsString("{}"));
    }

    @Test
    public void testDoDeleteWrong() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        final String sessionId = request.getSession().getId();
        when(accountService.logout(sessionId)).thenReturn(false);

        final SignInServlet signInServlet = new SignInServlet(context);
        signInServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(stringWriter.toString(),StringContains.containsString("{\"error\":\"This session is not registered\"}"));
    }
}