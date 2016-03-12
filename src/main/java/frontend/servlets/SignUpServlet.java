package frontend.servlets;

import com.google.gson.*;
import main.AccountService;
import main.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;


public class SignUpServlet extends HttpServlet {
    private AccountService accountService;

    public SignUpServlet() {
        this.accountService = AccountService.getInstance();
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

            final UserProfile newUser = accountService.createUser(login, password, email);

            if (newUser == null) {
                throw new Exception("Login already exist");

            }
            final String sessionId = request.getSession().getId();
            accountService.addSessions(sessionId, newUser);

            responseBody.add("id", new JsonPrimitive(newUser.getId()));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (JsonParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody.add("error", new JsonPrimitive(e.getMessage()));
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
            if (request.getPathInfo() == null)
                throw new Exception("Wrong request");

            final String requestUserId = request.getPathInfo().replace("/", "");

            if (requestUserId == null || !isInteger(requestUserId, 10)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new Exception("Wrong request");
            }

            final long userDbId = Integer.parseInt(requestUserId);
            final UserProfile requstedUser = accountService.getUserById(userDbId);

            final String currentSession = request.getSession().getId();
            final UserProfile currentUser = accountService.getSessions(currentSession);

            if (currentUser == null || !requstedUser.equals(currentUser)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                throw new Exception("Request from wrong user");
            }

            response.setStatus(HttpServletResponse.SC_OK);
            responseBody.add("id", new JsonPrimitive(currentUser.getId()));
            responseBody.add("login", new JsonPrimitive(currentUser.getLogin()));
            responseBody.add("email", new JsonPrimitive(currentUser.getEmail()));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseBody.add("error", new JsonPrimitive(e.getMessage()));
        } catch (Exception e) {
            responseBody.add("error", new JsonPrimitive(e.getMessage()));
        }
        response.getWriter().println(responseBody);
    }


    @Override
    public void doPut(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();
        final BufferedReader bufferedReader = request.getReader();

        final JsonStreamParser jsonParser = new JsonStreamParser(bufferedReader);
        try {
            if (request.getPathInfo() == null) {
                throw new Exception("Wrong request");
            }

            final String requestUserId = request.getPathInfo().replace("/", "");

            if (requestUserId == null || !isInteger(requestUserId, 10)) {
                throw new Exception("Wrong request");
            }

            final long userId = Integer.parseInt(requestUserId);
            final String currentUserSession = request.getSession().getId();

            if (accountService.getUserBySession(userId, currentUserSession)) {
                throw new Exception("Request from other user");
            }

            JsonElement message = new JsonObject();
            if (jsonParser.hasNext()) {
                message = jsonParser.next();
            }

            if (message.getAsJsonObject().get("login") == null
                    || message.getAsJsonObject().get("email") == null
                    || message.getAsJsonObject().get("password") == null) {
                throw new Exception("Pls enter all params");
            }

            final String login = message.getAsJsonObject().get("login").getAsString();
            final String email = message.getAsJsonObject().get("email").getAsString();
            final String password = message.getAsJsonObject().get("password").getAsString();

            if (!accountService.updateUser(userId, login, password, email)) {
                throw new Exception("User doesn't exist");
            }
            responseBody.add("id", new JsonPrimitive(userId));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException | JsonParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody.add("error", new JsonPrimitive(e.getMessage()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseBody.add("error", new JsonPrimitive(e.getMessage()));
        }

        response.getWriter().println(responseBody);
    }


    @Override
    public void doDelete(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();
        try {
            if (request.getPathInfo() == null)
                throw new Exception("Wrong request");

            final String requestUserId = request.getPathInfo().replace("/", "");

            if (requestUserId == null || !isInteger(requestUserId, 10)) {
                throw new Exception("Wrong params");

            } else {
                final long userId = Integer.parseInt(requestUserId);
                if (!accountService.deleteUser(userId)) {
                    throw new Exception("user doesnt exist");
                }
                response.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody.add("error", new JsonPrimitive(e.getMessage()));
        } catch (Exception e) {
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
}
