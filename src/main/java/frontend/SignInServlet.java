package frontend;

import main.AccountService;
import main.UserProfile;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

import org.json.simple.JSONObject;

public class SignInServlet extends HttpServlet {
    private AccountService accountService;
    private static final JSONParser JSON_PARSER = new JSONParser();

    public SignInServlet(AccountService accountService) {
        this.accountService = accountService;
    }

//    public void doGet(HttpServletRequest request,
//                      HttpServletResponse response) throws ServletException, IOException {
//        String name = request.getParameter("name");
//        String password = request.getParameter("password");
//
//        response.setStatus(HttpServletResponse.SC_OK);
//
//        Map<String, Object> pageVariables = new HashMap<>();
//        UserProfile profile = accountService.getUser(name);
//        if (profile != null && profile.getPassword().equals(password)) {
//            JSONObject answer = new JSONObject();
//            answer.put("status", 200);
//            answer.put("id","registeredId");
//        } else {
//            JSONObject answer = new JSONObject();
//            answer.put("status", 401);
//        }
//        response.getWriter().println(PageGenerator.getPage("authstatus.html", pageVariables));
//    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        BufferedReader bufferedReader = request.getReader();
        StringBuilder jb = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null)
            jb.append(line);

        JSONObject resp = new JSONObject();

        try {
            JSONObject message = (JSONObject) JSON_PARSER.parse(jb.toString());
            if (!message.containsKey("login")) {
                throw new Exception("Login required");
            }

            if (!message.containsKey("email")) {
                throw new Exception("Email required");
            }

            if (!message.containsKey("password")) {
                throw new Exception("Login required");
            }

            String login = (String) message.get("login");
            String email = (String) message.get("email");
            String password = (String) message.get("password");

            accountService.addUser(login, new UserProfile(login, password, email));
            // database check
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.put("errorMessage", "cannotparseJSON");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(resp.toJSONString());
    }
}
