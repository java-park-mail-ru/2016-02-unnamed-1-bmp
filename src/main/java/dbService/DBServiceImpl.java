package dbservice;

import base.DBService;
import base.datasets.UserDataSet;
import dbservice.dao.UserDataSetDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

public class DBServiceImpl implements DBService {
    private SessionFactory sessionFactory;

    public DBServiceImpl() {
        final Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/sea_battle");
        configuration.setProperty("hibernate.connection.username", "dev");
        configuration.setProperty("hibernate.connection.password", "12345678");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");

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
    public boolean saveUser(UserDataSet dataSet) {
        final Session session = sessionFactory.openSession();
        final Transaction transaction = session.beginTransaction();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        long returnedId = 0;
        try {
            returnedId = dao.save(dataSet);
            if (returnedId == -1) {
                 return false;
             }
        } catch (ConstraintViolationException e) {
            return false;
        }
        transaction.commit();
        dataSet.setId(returnedId);
        return true;
    }

    @Override
    public UserDataSet getUserById(long id) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        return dao.readById(id);
    }

    @Override
    public UserDataSet getUserByEmail(String email) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        return dao.readByEmail(email);
    }


    @Override
    public UserDataSet getUserByLogin(String login) {
        final Session session = sessionFactory.openSession();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        return dao.readByLogin(login);
    }


    @Override
    public boolean updateUserEmail(Long id, String email, String login, String pass) {
        final Session session = sessionFactory.openSession();
        final Transaction transaction = session.beginTransaction();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        if(!dao.updateEmail(id, email, login, pass)) {
            return false;
        }
        transaction.commit();
        return true;
    }

    @Override
    public boolean deleteUserById(Long id) {
        final Session session = sessionFactory.openSession();
        final Transaction transaction = session.beginTransaction();
        final UserDataSetDAO dao = new UserDataSetDAO(session);
        if(!dao.deleteById(id)) {
            return false;
        }
        transaction.commit();
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
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        final ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

}