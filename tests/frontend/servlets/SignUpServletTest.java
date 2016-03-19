package frontend.servlets;

import base.datasets.UserDataSet;
import org.hamcrest.core.StringContains;
import base.DBService;
import base.AccountService;
import main.AccountServiceImpl;
import dbservice.DBServiceImpl;
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
import org.mockito.internal.stubbing.BaseStubbing;


public class SignUpServletTest {
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
    public void setUp() throws Exception {
        context  = new Context();
        dbService = mock(DBServiceImpl.class);

        context.add(DBService.class, dbService);
    }

    @Test
    public void testDoPost() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();
        final AccountService accountService = mock(AccountServiceImpl.class);
        context.add(AccountService.class, accountService);

        final String input = "{\"login\":\"admin\",\"password\":\"admin\", \"email\":\"admin@admin.com\"}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));
        final UserDataSet newUser = mock(UserDataSet.class);

        when(request.getReader()).thenReturn(bufferedReader);
        when(dbService.getUserByLogin("admin")).thenReturn(newUser);
        when(dbService.saveUser(any(UserDataSet.class))).thenReturn(true);
        when(newUser.getId()).thenReturn(1L);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(stringWriter.toString(),StringContains.containsString("{\"id\":1"));
    }


    @Test
    public void testDoPostFail() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        final String input = "{\"login\":\"admin\",\"password\":\"admin\", \"email\":\"admin@admin.com\"}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));

        when(request.getReader()).thenReturn(bufferedReader);
        when(dbService.saveUser(any(UserDataSet.class))).thenReturn(false);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertThat(stringWriter.toString(),StringContains.containsString("{\"error\":\"Login already exist\""));
    }


    @Test
    public void testDoGet() throws IOException {

    }


    @Test
    public void testDoPut() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        final String input = "{\"login\":\"admin\",\"password\":\"admin\", \"email\":\"admin@admin.com\"}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));
        final UserDataSet newUser = mock(UserDataSet.class);

        when(request.getReader()).thenReturn(bufferedReader);
        when(request.getPathInfo()).thenReturn("/1");
        when(newUser.getId()).thenReturn(1L);
        when(dbService.updateUserEmail(anyLong(),anyString(), anyString(), anyString())).thenReturn(true);
        when(dbService.getUserById(1L)).thenReturn(newUser);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(stringWriter.toString(),StringContains.containsString("{\"id\":1"));
    }



    @Test
    public void testDoPutFail() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        final String input = "{\"login\":\"admin\", \"email\":\"admin@admin.com\"}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));
        final UserDataSet newUser = mock(UserDataSet.class);

        when(request.getReader()).thenReturn(bufferedReader);
        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }


    @Test
    public void testDoDelete() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();
        final UserDataSet newUser = mock(UserDataSet.class);

        when(request.getPathInfo()).thenReturn("/1");
        when(dbService.getUserById(anyLong())).thenReturn(newUser);
        when(dbService.deleteUserById(anyLong())).thenReturn(true);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }


    @Test
    public void testDoDeleteFail() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();
        final UserDataSet newUser = mock(UserDataSet.class);

        when(request.getPathInfo()).thenReturn("/1");
        when(dbService.getUserById(anyLong())).thenReturn(newUser);
        when(dbService.deleteUserById(anyLong())).thenReturn(false);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void testIsInteger() throws Exception {
        final boolean ret = SignUpServlet.isInteger("27",10);
        assertEquals(ret, true);
    }

    @Test
    public void testIsIntegerFail() throws Exception {
        final boolean ret = SignUpServlet.isInteger("lalala",10);
        assertEquals(ret, false);
    }

}