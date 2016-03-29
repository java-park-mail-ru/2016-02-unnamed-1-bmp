package frontend.servlets;

import base.AccountService;
import base.DBService;
import base.UserService;
import com.google.gson.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

import dbservice.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.Context;
import base.datasets.UserDataSet;

public class SignInServlet extends HttpServlet {
    public static final String PATH = "/api/session";
    private static final Logger LOGGER = LogManager.getLogger(SignInServlet.class);
    private AccountService accountService;
    private UserService userService;

    public SignInServlet(Context context) {
        this.userService = (UserService) context.get(UserService.class);
        this.accountService = (AccountService) context.get(AccountService.class);
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();

        final String sessionId = request.getSession().getId();
        final Long userId = accountService.getUserIdBySesssion(sessionId);

        if (userId != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            responseBody.add("id", new JsonPrimitive(userId));
            LOGGER.info("Get info about user with id: {}", userId);
        } else {
            goOut(response, responseBody, HttpServletResponse.SC_UNAUTHORIZED, "User not authorized");
        }
        response.getWriter().println(responseBody);
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();
        final BufferedReader bufferedReader = request.getReader();
        final JsonStreamParser jsonParser = new JsonStreamParser(bufferedReader);

        try {
            JsonElement message = new JsonObject();
            if (jsonParser.hasNext()) {
                message = jsonParser.next();
            }

            LOGGER.info("Incoming message: {}", message.toString());

            if (message.getAsJsonObject().get("login") == null
                    || message.getAsJsonObject().get("password") == null) {
                goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, "Not all params send");
                return;
            }

            final String login = message.getAsJsonObject().get("login").getAsString();

            final UserDataSet user = userService.getUserByLogin(login);
            if ( user == null ) {
                goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, "User doesn\'t exist");
                return;
            }

            final String sessionId = request.getSession().getId();
            accountService.addSessions(sessionId, user.getId());

            response.setStatus(HttpServletResponse.SC_OK);
            responseBody.add("id", new JsonPrimitive(user.getId()));
            LOGGER.info("Logged in user with id : {}", user.getId());

        } catch (JsonParseException e) {
            goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, "Wrong json");
        } catch (DatabaseException e) {
            goOut(response, responseBody, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Wrong request");
        }

        response.getWriter().println(responseBody);
    }

    @Override
    public void doDelete(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();
        final String sessionId = request.getSession().getId();

        if (!accountService.logout(sessionId)) {
            goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, "Fail to delete user session");
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            LOGGER.info("Deleted user session with sessionid {}", sessionId);
        }

        response.getWriter().println(responseBody);
    }

    private void  goOut (HttpServletResponse response, JsonObject responseBody,
                         int status, String error) {
        response.setStatus(status);
        responseBody.add("error", new JsonPrimitive(error));
        LOGGER.error(error);
    }
}

