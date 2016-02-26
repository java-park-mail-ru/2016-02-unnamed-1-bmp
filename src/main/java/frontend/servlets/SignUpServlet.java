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
    private static final JsonParser JSON_PARSER = new JsonParser();

    public SignUpServlet() {
        this.accountService = AccountService.getInstance();
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseBody = new JsonObject();

        BufferedReader bufferedReader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null)
            jsonBuilder.append(line);

        try {
            JsonObject message = JSON_PARSER.parse(jsonBuilder.toString()).getAsJsonObject();

            if (message.get("login") == null || message.get("email") == null
                    || message.get("password") == null) {
                throw new Exception("Not all params send");
            }

            String login = message.get("login").getAsString();
            String email = message.get("email").getAsString();
            String password = message.get("password").getAsString();

            UserProfile newUser = accountService.createUser(login, password, email);

            if (newUser == null) {
                throw new Exception("Login already exist");

            }
            String sessionId = request.getSession().getId();
            accountService.addSessions(sessionId, newUser);

            responseBody.add("id", new JsonPrimitive(newUser.getId()));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (JsonSyntaxException e) {
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
        JsonObject responseBody = new JsonObject();
        try {
            if (request.getPathInfo() == null)
                throw new Exception("Wrong request");

            String requestUserId = request.getPathInfo().replace("/", "");

            if (requestUserId == null || !isInteger(requestUserId, 10)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new Exception("Wrong request");
            }

            long userDbId = Integer.parseInt(requestUserId);
            UserProfile requstedUser = accountService.getUserById(userDbId);

            String currentSession = request.getSession().getId();
            UserProfile currentUser = accountService.getSessions(currentSession);

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
        JsonObject responseBody = new JsonObject();
        BufferedReader bufferedReader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null)
            jsonBuilder.append(line);

        try {
            if (request.getPathInfo() == null) {
                throw new Exception("Wrong request");
            }

            String requestUserId = request.getPathInfo().replace("/", "");

            if (requestUserId == null || !isInteger(requestUserId, 10)) {
                throw new Exception("Wrong request");
            }

            long userId = Integer.parseInt(requestUserId);
            String currentUserSession = request.getSession().getId();

            if (accountService.getUserBySession(userId, currentUserSession)) {
                throw new Exception("Request from other user");
            }

            JsonObject message = JSON_PARSER.parse(jsonBuilder.toString()).getAsJsonObject();

            if (message.get("login") == null || message.get("email") == null || message.get("password") == null) {
                throw new Exception("Pls enter all params");
            }

            String login = message.get("login").getAsString();
            String email = message.get("email").getAsString();
            String password = message.get("password").getAsString();

            if (!accountService.updateUser(userId, login, password, email)) {
                throw new Exception("User doesn't exist");
            }
            responseBody.add("id", new JsonPrimitive(userId));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NumberFormatException e) {
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
        JsonObject responseBody = new JsonObject();
        try {
            if (request.getPathInfo() == null)
                throw new Exception("Wrong request");

            String requestUserId = request.getPathInfo().replace("/", "");

            if (requestUserId == null || !isInteger(requestUserId, 10)) {
                throw new Exception("Wrong params");

            } else {
                long userId = Integer.parseInt(requestUserId);
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
