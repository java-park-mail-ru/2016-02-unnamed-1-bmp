package frontend.servlets;

import com.google.gson.*;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import main.AccountService;
import main.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class SignUpServlet extends HttpServlet {
    private AccountService accountService;
    private static final JsonParser JSON_PARSER = new JsonParser();

    public SignUpServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseBody = new JsonObject();

        BufferedReader bufferedReader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line = null;

        while ((line = bufferedReader.readLine()) != null)
            jsonBuilder.append(line);

        try {
            JsonObject message = JSON_PARSER.parse(jsonBuilder.toString()).getAsJsonObject();

            String login = message.get("login").getAsString();
            String email = message.get("email").getAsString();
            String password = message.get("password").getAsString();

            if (login == null) {
                throw new Exception("Login required");
            }

            if (email == null) {
                throw new Exception("Email required");
            }

            if (password == null) {
                throw new Exception("Password required");
            }

            if (accountService.getUser(login) != null ) {
                throw new Exception("Login already exist");
            }

            accountService.addUser(login, new UserProfile(login, password, email));
            responseBody.add("id", new JsonPrimitive(1));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody.add("error", new JsonPrimitive(e.toString()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseBody.add("error", new JsonPrimitive(e.toString()));
        }

        response.getWriter().println(responseBody);
    }

    @Override
    public void doGet(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseBody = new JsonObject();
        String requestUserId = request.getPathInfo().replace("/","");
        try {
            if (requestUserId == null || !isInteger(requestUserId, 10)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new Exception("Wrong request");
            }

            int userDbId = Integer.parseInt(requestUserId);
            //without database
            //get user by login
            UserProfile requstedUser = accountService.getUser("anyLogin");

            String currentSession = request.getSession().getId();
            UserProfile currentUser = accountService.getSessions(currentSession);

            if (currentUser == null || !requstedUser.equals(currentUser)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                throw new Exception("Request from wrong user");
            }

            response.setStatus(HttpServletResponse.SC_OK);
            responseBody.add("id", new JsonPrimitive(requestUserId));
            responseBody.add("login", new JsonPrimitive(currentUser.getLogin()));
            responseBody.add("email", new JsonPrimitive(currentUser.getEmail()));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseBody.add("error", new JsonPrimitive(e.toString()));
        } catch (Exception e) {
            responseBody.add("error", new JsonPrimitive(e.toString()));
        }
        response.getWriter().println(responseBody);
    }


    @Override
    public void doPut(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseBody = new JsonObject();
        String requestUserId = request.getPathInfo().replace("/","");

        BufferedReader bufferedReader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null)
            jsonBuilder.append(line);

        try {
            if (requestUserId == null || !isInteger(requestUserId, 10)) {
                throw new Exception("Wrong request");
            }
            int userId = Integer.parseInt(requestUserId);

            //need db connection to check user

            JsonObject message = JSON_PARSER.parse(jsonBuilder.toString()).getAsJsonObject();
            String login = message.get("login").getAsString();
            String email = message.get("email").getAsString();
            String password = message.get("password").getAsString();

            if (login == null) {
                throw new Exception("Login required");
            }

            if (email == null) {
                throw new Exception("Email required");
            }

            if (password == null) {
                throw new Exception("Password required");
            }

            if (accountService.getUser(login) != null ) {
                throw new Exception("Login already exist");
            }

            accountService.updateUser(userId, login, password, email);
            responseBody.add("id", new JsonPrimitive(userId));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NumberFormatException e) {
            responseBody.add("error", new JsonPrimitive(e.toString()));;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseBody.add("error", new JsonPrimitive(e.toString()));
        }

        response.getWriter().println(responseBody);
    }


    @Override
    public void doDelete(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseBody = new JsonObject();
        String requestUserId = request.getPathInfo().replace("/", "");

        if (requestUserId == null || !isInteger(requestUserId, 10)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseBody.add("error", new JsonPrimitive("wrong params"));
        } else {
            int userId = Integer.parseInt(requestUserId);
            //get user by id from database
            accountService.deleteUser("username");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        response.getWriter().println(responseBody);
    }


    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
