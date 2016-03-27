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

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;


import base.DBService;
import base.AccountService;
import dbservice.DBServiceImpl;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static final int DEFAULT_PORT = 8080;
    public static final int STATUS = 500;

    @SuppressWarnings("OverlyBroadThrowsClause")
    public static void main(String[] args) throws Exception {

        int port = DEFAULT_PORT;
        if (args.length == 1) {
            final String portString = args[0];
            port = Integer.valueOf(portString);
        }

        LOGGER.info("Starting server at port {}", String.valueOf(port));
        final Context classContext = new Context();

        final Configuration cfgDb = new Configuration().configure("dbconfig.xml");
        try {
            final DBService dbService = new DBServiceImpl(cfgDb);
            final AccountService accountService = new AccountServiceImpl();

            classContext.add(DBService.class, dbService);
            classContext.add(AccountService.class, accountService);

            final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.addServlet(new ServletHolder(new SignInServlet(classContext)), "/api/session");
            context.addServlet(new ServletHolder(new SignUpServlet(classContext)), "/api/user/*");
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
        } catch (HibernateException e) {
            LOGGER.info("Fail to connect to DB");
        }
    }
}