package main;

import base.*;
import dbservice.UserServiceImpl;
import frontend.WebSocketServiceImpl;
import frontend.servlets.ScoreboardServlet;
import frontend.servlets.SignInServlet;
import frontend.servlets.SignUpServlet;
import frontend.servlets.WebSocketGameServlet;
import game.GameMechanicsImpl;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.cfg.Configuration;


import dbservice.DBServiceImpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static final int DEFAULT_PORT = 8080;
    public static final String DEFAULT_HOST = "127.0.0.1";

    @SuppressWarnings("OverlyBroadThrowsClause")
    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;
        String host = DEFAULT_HOST;

        try (final FileInputStream serverProps = new FileInputStream("setups/server.properties")) {
            final Properties properties = new Properties();
            properties.load(serverProps);
            port = Integer.valueOf(properties.getProperty("port"));
            host = properties.getProperty("host");
        } catch (FileNotFoundException e) {
            LOGGER.error("Can't find server configuration file - starts with defaults", e);
        }
        LOGGER.info("Starting server at {}:{}", host, String.valueOf(port));

        final Context classContext = new Context();
        final Configuration cfgDb = new Configuration().configure("dbconfig.xml");
        DBService dbService = null;

        try {
            dbService = new DBServiceImpl(cfgDb);
        } catch (LaunchException e) {
            LOGGER.fatal("Can't connect to DB", e);
            return;
        }

        final WebSocketService webSocketService = new WebSocketServiceImpl();
        final GameMechanics gameMechanics = new GameMechanicsImpl(webSocketService);
        final UserService userService = new UserServiceImpl(dbService);
        final AccountService accountService = new AccountServiceImpl();

        classContext.add(GameMechanics.class, gameMechanics);
        classContext.add(WebSocketService.class, webSocketService);
        classContext.add(UserService.class, userService);
        classContext.add(AccountService.class, accountService);

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new SignInServlet(classContext)), "/api/session");
        context.addServlet(new ServletHolder(new SignUpServlet(classContext)), "/api/user/*");
        context.addServlet(new ServletHolder(new ScoreboardServlet(classContext)), "/api/scoreboard");
        context.addServlet(new ServletHolder(new WebSocketGameServlet(classContext)), "/gameplay");
        LOGGER.info("Created servlets");

        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("dist");

        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, context});

        final Server server = new Server();
        final ServerConnector http = new ServerConnector(server);
        http.setHost(host);
        http.setPort(port);

        server.addConnector(http);
        server.setHandler(handlers);

        server.start();
        gameMechanics.run();
    }
}
