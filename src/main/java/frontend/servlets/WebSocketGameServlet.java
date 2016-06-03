package frontend.servlets;

import frontend.GameWebSocketCreator;
import main.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "WebSocketGameServlet", urlPatterns = {"/gameplay"})
public class WebSocketGameServlet extends WebSocketServlet {
    private static final Logger LOGGER = LogManager.getLogger(WebSocketGameServlet.class);
    private static final int LOGOUT_TIME = 5 * 60 * 1000;
    final Context context;

    public WebSocketGameServlet(Context context) {
        this.context = context;
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(LOGOUT_TIME);
        factory.setCreator(new GameWebSocketCreator(context));
        LOGGER.info("Socket servlet configured");
    }
}
