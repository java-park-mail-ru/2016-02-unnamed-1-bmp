package frontend.servlets;

import base.AccountService;
import base.UserService;
import com.google.gson.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

import dbservice.DatabaseException;
import main.Context;
import base.datasets.UserDataSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SignUpServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger(SignUpServlet.class);
    private final AccountService accountService;
    private final UserService userService;

    public enum AnimalPlayer {
        CAMEL,
        CHUPACABRA,
        GIRAFFE,
        MONKEY,
        GRIZZLY,
        CHAMELEON,
        ELEPHANT,
        HYENA,
        FROG,
        SHEEP,
        TURTLE,
        IGUANA,
        LEMUR,
        HIPPO,
        COYOTE,
        WOLF,
        PANDA,
        PYTHON;

        private static final int SIZE = AnimalPlayer.values().length;
        private static final Random RANDOM = new Random();

        public static AnimalPlayer randomAnimal() {
            return AnimalPlayer.values()[RANDOM.nextInt(SIZE)];
        }
    }

    public SignUpServlet(Context context) {
        this.userService = (UserService) context.get(UserService.class);
        this.accountService = (AccountService) context.get(AccountService.class);
    }

    private void doPostAnonymous(JsonElement message, HttpServletRequest request,
                                HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();

        LOGGER.info("Incoming message: {}", message.toString());
        if (message.getAsJsonObject().get("login") == null) {
            goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, "Not all params send");
            response.getWriter().println(responseBody);
            return;
        }

        final String login = message.getAsJsonObject().get("login").getAsString().trim();
        final long newUserId;
        try {
            final String realLogin = login.isEmpty() ? "Anonymous " + AnimalPlayer.randomAnimal().toString().toLowerCase() : login;
            newUserId = userService.saveUser(new UserDataSet(realLogin));
            final String sessionId = request.getSession().getId();
            accountService.addSessions(sessionId, newUserId);
        } catch (DatabaseException e) {
            goOutDatabseException(response, responseBody,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), e);
            response.getWriter().println(responseBody);
            return;
        }
        responseBody.add("id", new JsonPrimitive(newUserId));
        response.setStatus(HttpServletResponse.SC_OK);
        LOGGER.info("Register anonymous user {}", login);
        response.getWriter().println(responseBody);
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();
        final BufferedReader bufferedReader = request.getReader();
        final JsonStreamParser jsonParser = new JsonStreamParser(bufferedReader);
        JsonElement message = new JsonObject();
        if (!message.isJsonObject()) {
            goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, "Not a JSON");
            response.getWriter().println(responseBody);
            return;
        }

        try {
            if (jsonParser.hasNext()) message = jsonParser.next();
        } catch (JsonParseException e) {
            goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, "Can\'t parse JSON");
            response.getWriter().println(responseBody);
            return;
        }

        LOGGER.info("Incoming message: {}", message.toString());

        final JsonElement isAnonymous = message.getAsJsonObject().get("isAnonymous");
        if(isAnonymous != null && isAnonymous.getAsBoolean()) {
            this.doPostAnonymous(message, request, response);
            return;
        }

        if (message.getAsJsonObject().get("login") == null || message.getAsJsonObject().get("email") == null
                || message.getAsJsonObject().get("password") == null) {
            goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, "Not all params send");
            response.getWriter().println(responseBody);
            return;
        }

        final String login = message.getAsJsonObject().get("login").getAsString();
        final String email = message.getAsJsonObject().get("email").getAsString();
        final String password = message.getAsJsonObject().get("password").getAsString();
        final long newUserId;
        try {
            if (!userService.isEmailUnique(email)) {
                goOutFieldError(response, responseBody, HttpServletResponse.SC_FORBIDDEN,
                        "Email already exist", "email");
                response.getWriter().println(responseBody);
                return;
            }
            if (!userService.isLoginUnique(login)) {
                goOutFieldError(response, responseBody, HttpServletResponse.SC_BAD_REQUEST,
                        "Login already exist", "login");
                response.getWriter().println(responseBody);
                return;
            }
            newUserId = userService.saveUser(new UserDataSet(login, password, email));
            final String sessionId = request.getSession().getId();
            accountService.addSessions(sessionId, newUserId);
        } catch (DatabaseException e) {
            goOutDatabseException(response, responseBody,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), e);
            response.getWriter().println(responseBody);
            return;
        }
        responseBody.add("id", new JsonPrimitive(newUserId));
        response.setStatus(HttpServletResponse.SC_OK);
        LOGGER.info("Register user {}", login);
        response.getWriter().println(responseBody);
    }


    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();
        final UserDataSet currUser;
        try {
            currUser = checkRequest(request);
        } catch (NumberFormatException e) {
            goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            response.getWriter().println(responseBody);
            return;
        } catch (DatabaseException e) {
            goOutDatabseException(response, responseBody,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), e);
            response.getWriter().println(responseBody);
            return;
        }

        if (currUser == null) {
            goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, "User doesn\'t exist");
            response.getWriter().println(responseBody);
            return;
        }

        final Long currUserId = currUser.getId();
        response.setStatus(HttpServletResponse.SC_OK);
        responseBody.add("id", new JsonPrimitive(currUserId));
        responseBody.add("login", new JsonPrimitive(currUser.getLogin()));
        responseBody.add("email", new JsonPrimitive(currUser.getEmail()));
        responseBody.add("isAnonymous", new JsonPrimitive(currUser.getIsAnonymous()));
        LOGGER.info("Get info about user {}", currUser.getLogin());
        response.getWriter().println(responseBody);
    }


    @Override
    public void doDelete(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        final JsonObject responseBody = new JsonObject();

        try {
            final UserDataSet currUser = checkRequest(request);
            if (currUser == null || !userService.deleteUserById(currUser.getId()) ) {
                goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, "User doesn\'t exist");
                response.getWriter().println(responseBody);
                return;
            } else {
                accountService.logoutFull(currUser.getId());
                response.setStatus(HttpServletResponse.SC_OK);
                LOGGER.info("Deleted user with id {}", currUser.getId());
            }
        } catch (NumberFormatException e) {
            goOut(response, responseBody, HttpServletResponse.SC_BAD_REQUEST, "Wrong request");
        } catch (DatabaseException e) {
            goOutDatabseException(response, responseBody, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "This session is not registered", e);
        }

        response.getWriter().println(responseBody);
    }

    public UserDataSet checkRequest(HttpServletRequest request) throws DatabaseException {
        if (request.getPathInfo() == null)
            return null;
        final String requestUserId = request.getPathInfo().replace("/", "");
        if (requestUserId == null || !requestUserId.matches("^\\d+$"))
            return null;
        final long userDbId;
        try {
            userDbId = Integer.parseInt(requestUserId);
        } catch (NumberFormatException ignore) {
            return null;
        }
        return userService.getUserById(userDbId);
    }

    private void goOut(HttpServletResponse response, JsonObject responseBody,
                       int status, String error) {
        response.setStatus(status);
        responseBody.add("error", new JsonPrimitive(error));
        LOGGER.debug(error);
    }

    private void goOutFieldError(HttpServletResponse response, JsonObject responseBody,
                                 int status, String error, String field) {
        response.setStatus(status);
        responseBody.add("error", new JsonPrimitive(error));
        responseBody.add("field", new JsonPrimitive(field));
        LOGGER.debug(error);
    }

    private void goOutDatabseException(HttpServletResponse response, JsonObject responseBody,
                                       int status, String error, DatabaseException exc) {
        response.setStatus(status);
        responseBody.add("error", new JsonPrimitive(error));
        LOGGER.debug(error, exc);
    }
}
