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


public class SignInServlet extends HttpServlet {
    private AccountService accountService;
    private static final JsonParser JSON_PARSER = new JsonParser();

    public SignInServlet() {
        this.accountService = AccountService.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseBody = new JsonObject();

        String sessionId = request.getSession().getId();
        UserProfile user = accountService.getSessions(sessionId);

        if (user != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            responseBody.add("id", new JsonPrimitive(user.getId()));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responseBody.add("error", new JsonPrimitive("User not authorized"));
        }
        response.getWriter().println(responseBody);
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseBody = new JsonObject();
        BufferedReader bufferedReader = request.getReader();
        StringBuilder jb = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null)
            jb.append(line);

        try {
            JsonObject message = JSON_PARSER.parse(jb.toString()).getAsJsonObject();

            if (message.get("login") == null || message.get("password")== null ) {
                throw new Exception("Not all params send");
            }

            String login = message.get("login").getAsString();
            String password = message.get("password").getAsString();

            UserProfile user = accountService.getUser(login);
            if (user == null){
                throw new Exception("No such user");
            }

            String sessionId = request.getSession().getId();
            accountService.addSessions(sessionId, user);

            response.setStatus(HttpServletResponse.SC_OK);
            responseBody.add("id", new JsonPrimitive(user.getId()));

        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody.add("error", new JsonPrimitive("wrong json"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseBody.add("error", new JsonPrimitive(e.toString()));
        }

        response.getWriter().println(responseBody);
    }
}
