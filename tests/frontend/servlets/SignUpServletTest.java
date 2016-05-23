package frontend.servlets;

import base.datasets.UserDataSet;
import dbservice.DatabaseException;
import frontend.FrontendTest;
import org.hamcrest.core.StringContains;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Test;


public class SignUpServletTest extends FrontendTest {

    @Test
    public void testDoPost() throws IOException, ServletException, DatabaseException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();
        final UserDataSet userDataSet = mock(UserDataSet.class);

        final String input = "{\"login\":\"admin\",\"password\":\"admin\", \"email\":\"admin@admin.com\"}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));

        when(request.getReader()).thenReturn(bufferedReader);
        when(userService.getUserByLogin("admin")).thenReturn(userDataSet);
        when(userService.isEmailUnique("admin@admin.com")).thenReturn(true);
        when(userService.isLoginUnique("admin")).thenReturn(true);

        when(userService.saveUser(any(UserDataSet.class))).thenReturn(1L);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(stringWriter.toString(), StringContains.containsString("\"id\":1"));
    }


    @Test
    public void testDoPostAnonymous() throws IOException, ServletException, DatabaseException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();
        final UserDataSet userDataSet = mock(UserDataSet.class);

        final String input = "{\"login\":\"user\",\"isAnonymous\":true}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));

        when(request.getReader()).thenReturn(bufferedReader);
        when(userService.getUserByLogin("user")).thenReturn(userDataSet);
        when(userService.isLoginUnique("user")).thenReturn(false);
        when(userService.saveUser(any(UserDataSet.class))).thenReturn(1L);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(stringWriter.toString(), StringContains.containsString("\"id\":1"));
    }


    @Test
    public void testDoPostNotAnonymous() throws IOException, ServletException, DatabaseException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();
        final UserDataSet userDataSet = mock(UserDataSet.class);

        final String input = "{\"login\":\"admin\",\"password\":\"admin\", \"email\":\"admin@admin.com\", \"isAnonymous\":false}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));

        when(request.getReader()).thenReturn(bufferedReader);
        when(userService.getUserByLogin("admin")).thenReturn(userDataSet);
        when(userService.isEmailUnique("admin@admin.com")).thenReturn(true);
        when(userService.isLoginUnique("admin")).thenReturn(true);

        when(userService.saveUser(any(UserDataSet.class))).thenReturn(1L);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(stringWriter.toString(), StringContains.containsString("\"id\":1"));
    }


    @Test
    public void testDoPostCantParse() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        final String input = "{login\":\"admin\",\"password:\"admin\", \"email:\"admin@admin.com\"}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));

        when(request.getReader()).thenReturn(bufferedReader);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(stringWriter.toString(), StringContains.containsString("{\"error\":\"Can't parse JSON\""));
    }


    @Test
    public void testDoPostEmptyField() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        final String input = "{\"login\":\"admin\"}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));

        when(request.getReader()).thenReturn(bufferedReader);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }


    @Test
    public void testDoPostAlreadyExist() throws IOException, ServletException, DatabaseException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();
        final UserDataSet userDataSet = mock(UserDataSet.class);

        final String input = "{\"login\":\"admin\",\"password\":\"admin\", \"email\":\"admin@admin.com\"}";
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IOUtils.toInputStream(input)));

        when(request.getReader()).thenReturn(bufferedReader);
        when(userService.getUserByLogin("admin")).thenReturn(userDataSet);
        when(userService.isEmailUnique("admin@admin.com")).thenReturn(false);
        when(userService.isLoginUnique("admin")).thenReturn(false);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertThat(stringWriter.toString(), StringContains.containsString("{\"error\":\"Данный email уже зарегистрирован\""));
        assertThat(stringWriter.toString(), StringContains.containsString("\"field\":\"email\""));
    }


    @Test
    public void testDoGet() throws IOException, ServletException, DatabaseException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();
        final UserDataSet newUser = mock(UserDataSet.class);

        when(request.getPathInfo()).thenReturn("/1");
        when(userService.getUserById(anyLong())).thenReturn(newUser);
        when(newUser.getId()).thenReturn(1L);
        when(newUser.getLogin()).thenReturn("admin");
        when(newUser.getEmail()).thenReturn("admin");

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(stringWriter.toString(), StringContains.containsString("{\"id\":1"));
    }


    @Test
    public void testDoGetNoId() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        when(request.getPathInfo()).thenReturn("");

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }


    @Test
    public void testDoDelete() throws IOException, ServletException, DatabaseException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();
        final UserDataSet newUser = mock(UserDataSet.class);

        when(request.getPathInfo()).thenReturn("/1");
        when(userService.getUserById(anyLong())).thenReturn(newUser);
        when(userService.deleteUserById(anyLong())).thenReturn(true);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertThat(stringWriter.toString(), StringContains.containsString("{}"));
    }


    @Test
    public void testDoDeleteFail() throws IOException, ServletException, DatabaseException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();
        final UserDataSet newUser = mock(UserDataSet.class);

        when(request.getPathInfo()).thenReturn("/1");
        when(userService.getUserById(anyLong())).thenReturn(newUser);
        when(userService.deleteUserById(anyLong())).thenReturn(false);

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }


    @Test
    public void testDoDeleteNumExept() throws IOException, ServletException {
        final StringWriter stringWriter = new StringWriter();
        final HttpServletResponse response = getMockedResponse(stringWriter);
        final HttpServletRequest request = getMockedRequest();

        when(request.getPathInfo()).thenReturn("/111111111111111111111111");

        final SignUpServlet signUpServlet = new SignUpServlet(context);
        signUpServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(stringWriter.toString(), StringContains.containsString("{\"error\":\"User doesn't exist\""));
    }


}