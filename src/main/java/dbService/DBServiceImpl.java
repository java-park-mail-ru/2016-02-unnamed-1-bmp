package dbservice;

import base.DBService;
import base.datasets.UserDataSet;
import dbservice.dao.UserDataSetDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.service.spi.ServiceException;

import java.util.List;

public class DBServiceImpl implements DBService {
    private static final Logger LOGGER = LogManager.getLogger(DBService.class);
    private SessionFactory sessionFactory;

    public DBServiceImpl(Configuration configuration) throws ServiceException {
        configuration.addAnnotatedClass(UserDataSet.class);

        LOGGER.info("Configuring database...");
        sessionFactory = createSessionFactory(configuration);
    }

    @Override
    public String getLocalStatus() {
        final String status;
        try (Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();
            status = transaction.getStatus().toString();
            transaction.commit();
        }
        return status;
    }

    @Override
    public long saveUser(UserDataSet dataSet) {
        final Session session = sessionFactory.openSession();
        final Transaction transaction = session.beginTransaction();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        long returnedId = 0;
        try {
            returnedId = dao.save(dataSet);
            if (returnedId == -1) {
                LOGGER.error("Fail to add new user");
                return -1;
             }
        } catch (ConstraintViolationException e) {
            LOGGER.error("Wrong request to database");
            return -1;
        }
        transaction.commit();
        dataSet.setId(returnedId);
        LOGGER.info("Saved user with login {}", dataSet.getLogin());
        return returnedId;
    }

    @Override
    public UserDataSet getUserById(long id) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        LOGGER.info("Get user info with id {}", id);
        return dao.readById(id);
    }

    @Override
    public UserDataSet getUserByEmail(String email) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        LOGGER.info("Get user info with email {}", email);
        return dao.readByEmail(email);
    }


    @Override
    public UserDataSet getUserByLogin(String login) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        LOGGER.info("Get user info with login {}", login);
        return dao.readByLogin(login);
    }


    @Override
    public boolean updateUserInfo(Long id, String email, String login, String pass) {
        final Session session = sessionFactory.openSession();
        final Transaction transaction = session.beginTransaction();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        if(!dao.updateEmail(id, email, login, pass)) {
            LOGGER.error("Failed to update user #{}", id);
            return false;
        }
        transaction.commit();
        LOGGER.info("Updated user #{}  with info: {}, {}", id, email, login);
        return true;
    }

    @Override
    public boolean deleteUserById(Long id) {
        final Session session = sessionFactory.openSession();
        final Transaction transaction = session.beginTransaction();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        if(!dao.deleteById(id)) {
            LOGGER.error("Fail to delete user #{}", id);
            return false;
        }
        transaction.commit();
        LOGGER.info("Deleted user #{}", id);
        return true;
    }

    @Override
    public List<UserDataSet> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.readAll();
        }
    }

    @Override
    public void shutdown() {
        sessionFactory.close();
        LOGGER.info("Stutdown database connection");
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        final ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

}