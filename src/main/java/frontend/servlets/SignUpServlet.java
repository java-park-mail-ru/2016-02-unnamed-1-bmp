package frontend.servlets;

import base.AccountService;
import com.google.gson.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

import main.Context;
import base.DBService;
import base.datasets.UserDataSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SignUpServlet extends HttpServlet {
    public static final String PATH = "/api/user/*";
    private static final Logger LOGGER = LogManager.getLogger();
    private AccountService accountService;
    private DBService dbService;

    public SignUpServlet(Context context) {
        this.dbService = (DBService) context.get(DBService.class);
        this.accountService = (AccountService) context.get(AccountService.class);
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

            if (message.getAsJsonObject().get("login") == null || message.getAsJsonObject().get("email") == null
                    || message.getAsJsonObject().get("password") == null) {
                throw new Exception("Not all params send");
            }

            final String login = message.getAsJsonObject().get("login").getAsString();
            final String email = message.getAsJsonObject().get("email").getAsString();
            final String password = message.getAsJsonObject().get("password").getAsString();

            final UserDataSet newUser = new UserDataSet(login, password, email);
            final boolean alreadyExist = dbService.saveUser(newUser) != -1;

            if (!alreadyExist) {
                throw new Exception("Login already exist");
            }

            final String sessionId = request.getSession().getId();
            final Long newUserId = dbService.getUserByLogin(login).getId();
            accountService.addSessions(sessionId, newUserId);

            responseBody.add("id", new JsonPrimitive(newUserId));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (JsonParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody.add("error", new JsonPrimitive("Wrong JSON"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseBody.add("error", new JsonPrimitive(e.getMessage()));
        }

        response.getWriter().println(responseBody);
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();
        try {
            final UserDataSet currUser = checkRequest(request);
            final Long currUserId = currUser.getId();

            response.setStatus(HttpServletResponse.SC_OK);
            responseBody.add("id", new JsonPrimitive(currUserId));
            responseBody.add("login", new JsonPrimitive(currUser.getLogin()));
            responseBody.add("email", new JsonPrimitive(currUser.getEmail()));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody.add("error", new JsonPrimitive(e.getMessage()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responseBody.add("error", new JsonPrimitive(e.getMessage()));
        }
        response.getWriter().println(responseBody);
    }


    @Override
    public void doPut(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();
        try {
            final UserDataSet currUser = checkRequest(request);
            final BufferedReader bufferedReader = request.getReader();
            final JsonStreamParser jsonParser = new JsonStreamParser(bufferedReader);

            JsonElement message = new JsonObject();
            if (jsonParser.hasNext()) {
                message = jsonParser.next();
            }

            if (message.getAsJsonObject().get("login") == null
                    || message.getAsJsonObject().get("email") == null
                    || message.getAsJsonObject().get("password") == null) {
                throw new Exception("Not all params send");
            }

            final String login = message.getAsJsonObject().get("login").getAsString();
            final String email = message.getAsJsonObject().get("email").getAsString();
            final String password = message.getAsJsonObject().get("password").getAsString();

            if (!dbService.updateUserInfo(currUser.getId(), email, login, password)) {
                throw new Exception("User doesn't exist");
            }
            responseBody.add("id", new JsonPrimitive(currUser.getId()));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException | JsonParseException | IOException e) {
            LOGGER.error(e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody.add("error", new JsonPrimitive("Wrong request"));
        } catch (Exception e) {
            LOGGER.error(e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody.add("error", new JsonPrimitive(e.getMessage()));
        }

        response.getWriter().println(responseBody);
    }


    @Override
    public void doDelete(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();
        try {
            final UserDataSet currUser = checkRequest(request);
            if (!dbService.deleteUserById(currUser.getId())) {
                throw new Exception("User doesn\'t exist");
            }
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException e) {
            LOGGER.error(e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody.add("error", new JsonPrimitive("Wrong request"));
        } catch (Exception e) {
            LOGGER.error(e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseBody.add("error", new JsonPrimitive(e.getMessage()));
        }
        response.getWriter().println(responseBody);
    }


    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    public UserDataSet checkRequest(HttpServletRequest request) throws Exception, NumberFormatException {

        if (request.getPathInfo() == null)
            throw new NumberFormatException("Wrong request");

        final String requestUserId = request.getPathInfo().replace("/", "");

        if (requestUserId == null || !isInteger(requestUserId, 10)) throw new NumberFormatException("Wrong userId");

        final long userDbId = Integer.parseInt(requestUserId);

        final String currentSessionId = request.getSession().getId();
        final UserDataSet currUser = dbService.getUserById(userDbId);

//              in future - for checking original user
//            if (!accountService.getUserBySession(userId, currentUserSession)) {
//                throw new Exception("Request from other user");
//            }

        if (currUser == null) throw new Exception("Wrong user");

        return  currUser;
    }
}