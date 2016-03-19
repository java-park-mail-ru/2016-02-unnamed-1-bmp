package main;

import frontend.servlets.SignInServlet;
import frontend.servlets.SignUpServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.Servlet;


import base.DBService;
import base.AccountService;
import dbservice.DBServiceImpl;

import java.util.List;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int DEFAULT_PORT = 8080;

    @SuppressWarnings("OverlyBroadThrowsClause")
    public static void main(String[] args) throws Exception {

        int port = DEFAULT_PORT;
        if (args.length == 1) {
            final String portString = args[0];
            port = Integer.valueOf(portString);
        }

        LOGGER.info("Starting server at port {}", String.valueOf(port));
        final Context classContext = new Context();

        final DBService dbService = new DBServiceImpl();
        final AccountService accountService = new AccountServiceImpl();

        classContext.add(DBService.class, dbService);
        classContext.add(AccountService.class, accountService);


        final Servlet signIn = new SignInServlet(classContext);
        final Servlet signUp = new SignUpServlet(classContext);

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(signIn), "/api/session");
        context.addServlet(new ServletHolder(signUp), "/api/user/*");
        LOGGER.info("Created servlets");

        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("public_html");

        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, context});

        final Server server = new Server(port);
        server.setHandler(handlers);

        server.start();
        server.join();
        dbService.shutdown();
    }
}