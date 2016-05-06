package frontend.servlets;

import base.UserService;
import base.datasets.UserDataSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dbservice.DatabaseException;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class ScoreboardServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger(ScoreboardServlet.class);
    private final UserService userService;

    public ScoreboardServlet(Context context) {
        this.userService = (UserService) context.get(UserService.class);
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");

        final JsonArray usersArray = new JsonArray();
        final List<UserDataSet> topTen;
        try {
            topTen = userService.getTop();
        } catch (DatabaseException e) {
            LOGGER.error(e);
            response.getWriter().println(usersArray);
            return;
        }

        for (UserDataSet user : topTen) {
            final JsonObject userObject = new JsonObject();
            userObject.add("name", new JsonPrimitive(user.getLogin()));
            userObject.add("score", new JsonPrimitive(user.getScore()));
            usersArray.add(userObject);
        }
        response.getWriter().println(usersArray);
    }

}
