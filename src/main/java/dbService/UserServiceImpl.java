package dbservice;

import base.DBService;
import base.UserService;
import base.datasets.UserDataSet;
import dbservice.dao.UserDataSetDAO;
import org.hibernate.HibernateException;

import java.util.List;


public class UserServiceImpl implements UserService {
    private DBService dbService;

    public UserServiceImpl(DBService dbService) throws HibernateException {
        this.dbService = dbService;
    }

    @Override
    public void saveUser(UserDataSet dataSet) throws DatabaseException {
        dbService.doWork((session)-> {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            dao.save(dataSet);
        });
    }

    @Override
    public boolean incrementUserScore(long id) throws DatabaseException {
        return dbService.doReturningWork((session)-> {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.incrementScore(id);
        });
    }

    @Override
    public UserDataSet getUserById(long id) throws DatabaseException {
        return dbService.doReturningWork((session) -> {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.readById(id);
        });
    }

    @Override
    public UserDataSet getUserByEmail(String email) throws DatabaseException {
        return dbService.doReturningWork((session) -> {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.readByEmail(email);
        });
    }


    @Override
    public UserDataSet getUserByLogin(String login) throws DatabaseException {
        return dbService.doReturningWork((session) -> {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.readByLogin(login);
        });
    }

    @Override
    public boolean deleteUserById(Long id) throws DatabaseException {
        return dbService.doReturningWork((session)-> {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.markAsDeletedById(id);
        });
    }

    @Override
    public List<UserDataSet> getUsers(boolean getAll) throws DatabaseException {
        return dbService.doReturningWork((session)-> {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.readAll(getAll);
        });
    }

    @Override
    public List<UserDataSet> getTop() throws DatabaseException {
        return dbService.doReturningWork((session)-> {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.getTopTen();
        });
    }


    @Override
    public boolean isEmailUnique(String email) throws DatabaseException {
        return dbService.doReturningWork((session)-> {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.checkUniqueEmail(email);
        });
    }

    @Override
    public boolean isLoginUnique(String login) throws DatabaseException {
        return dbService.doReturningWork((session)-> {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.checkUniqueLogin(login);
        });
    }

}
