package main;

import base.AccountService;
import base.DBService;
import base.UserService;
import base.WebSocketService;
import dbservice.DBServiceImpl;
import dbservice.UserServiceImpl;
import frontend.WebSocketServiceImpl;
import frontend.servlets.ScoreboardServlet;
import frontend.servlets.SignInServlet;
import frontend.servlets.SignUpServlet;
import frontend.servlets.WebSocketGameServlet;
import game.*;
import messagesystem.MessageSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hibernate.cfg.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static final int DEFAULT_PORT = 8080;
    public static final String DEFAULT_HOST = "127.0.0.1";
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    @SuppressWarnings("OverlyBroadThrowsClause")
    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;
        String host = DEFAULT_HOST;
        try (final InputStream stream = Main.class.getClassLoader().getResourceAsStream("serv.properties")) {
            final Properties properties = new Properties();
            properties.load(stream);
            port = Integer.valueOf(properties.getProperty("port"));
            host = properties.getProperty("host");
        } catch (FileNotFoundException e) {
            LOGGER.error("Can't find server configuration file - starts with defaults", e);
        }
        LOGGER.info("Starting server at {}:{}", host, String.valueOf(port));

        final Context classContext = new Context();
        final Configuration cfgDb = new Configuration().configure("dbconfig.xml");
        final DBService dbService;

        try {
            dbService = new DBServiceImpl(cfgDb);
        } catch (LaunchException e) {
            LOGGER.fatal("Can't connect to DB", e);
            return;
        }

        final UserService userService = new UserServiceImpl(dbService);
        final AccountService accountService = new AccountServiceImpl();

        classContext.add(UserService.class, userService);
        classContext.add(AccountService.class, accountService);

        final MessageSystem messageSystem = new MessageSystem();
        classContext.add(MessageSystem.class, messageSystem);

        final GameMechanics gameMechanicsOne = new GameMechanicsImpl(classContext);
        final GameMechanics gameMechanicsTwo = new GameMechanicsImpl(classContext);
        final WebSocketService webSocketService = new WebSocketServiceImpl(classContext);

        messageSystem.addService(gameMechanicsOne);
        messageSystem.addService(gameMechanicsTwo);
        messageSystem.addService(webSocketService);

        messageSystem.getAddressService().registerGameMechanics(gameMechanicsOne);
        messageSystem.getAddressService().registerGameMechanics(gameMechanicsTwo);
        messageSystem.getAddressService().registerWebSocketService(webSocketService);

        final Thread gameMechanicsThreadOne = new Thread(gameMechanicsOne);
        gameMechanicsThreadOne.setDaemon(true);
        gameMechanicsThreadOne.setName("First game mechanics");

        final Thread gameMechanicsThreadTwo = new Thread(gameMechanicsTwo);
        gameMechanicsThreadTwo.setDaemon(true);
        gameMechanicsThreadTwo.setName("Second game mechanics");

        final Thread webSocketServiceThread = new Thread(webSocketService);
        webSocketServiceThread.setDaemon(true);
        webSocketServiceThread.setName("Web socket");

        gameMechanicsThreadOne.start();
        gameMechanicsThreadTwo.start();
        webSocketServiceThread.start();
        LOGGER.info("Started threads");

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new SignInServlet(classContext)), "/api/session");
        context.addServlet(new ServletHolder(new SignUpServlet(classContext)), "/api/user/*");
        context.addServlet(new ServletHolder(new ScoreboardServlet(classContext)), "/api/scoreboard");
        context.addServlet(new ServletHolder(new WebSocketGameServlet(classContext)), "/gameplay");
        LOGGER.info("Created servlets");

        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{ context });

        final Server server = new Server();
        final ServerConnector http = new ServerConnector(server);
        http.setHost(host);
        http.setPort(port);
        server.addConnector(http);
        server.setHandler(handlers);

        server.start();
        server.join();
    }
}
