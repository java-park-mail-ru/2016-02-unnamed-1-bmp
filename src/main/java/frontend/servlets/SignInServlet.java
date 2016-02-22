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

    public SignInServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void doGet(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseBody = new JsonObject();

        String sessionId = request.getSession().getId();
        UserProfile user = accountService.getSessions(sessionId);

        if (user != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            responseBody.add("id", new JsonPrimitive(1));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responseBody.add("error", new JsonPrimitive(sessionId));
        }
        response.getWriter().println(responseBody);
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        BufferedReader bufferedReader = request.getReader();
        StringBuilder jb = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null)
            jb.append(line);

        JsonObject responseBody = new JsonObject();

        try {
            JsonObject message = JSON_PARSER.parse(jb.toString()).getAsJsonObject();

            String login = message.get("login").getAsString();
            String password = message.get("password").getAsString();

            //check incoming JSON
            if (login == null) {
                throw new Exception("Login required");
            }

            if (password == null) {
                throw new Exception("Password required");
            }

            UserProfile user = accountService.getUser(login);
            if (user != null){
                throw new Exception("User already exist");
            }
            // database check here in future

            //add session for user
            String sessionId = request.getSession().getId();
            accountService.addSessions(sessionId, user);

            response.setStatus(HttpServletResponse.SC_OK);
            responseBody.add("id", new JsonPrimitive(1));

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
