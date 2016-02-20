package frontend;

import main.AccountService;
import main.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HelloServlet extends HttpServlet {
    private AccountService accountService;

    public HelloServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        HttpSession session =  request.getSession();
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) {
//            userId = userIdGenerator;
        }
        Map<String, Object> pageVariables = new HashMap<>();
        //UserProfile profile = accountService.getUser(name);
    }
}
