package base;


import base.datasets.UserDataSet;
import dbservice.DatabaseException;

import java.util.List;

public interface UserService {
    Long saveUser (UserDataSet dataSet) throws DatabaseException;

    void incrementUserScore(long id) throws DatabaseException;

    UserDataSet getUserById (long id) throws DatabaseException;

    UserDataSet getUserByEmail(String email) throws DatabaseException;

    UserDataSet getUserByLogin(String login) throws DatabaseException;

    boolean deleteUserById(Long id) throws DatabaseException;

    List<UserDataSet> getTop() throws DatabaseException;

    boolean isEmailUnique(String email) throws DatabaseException;

    boolean isLoginUnique(String login) throws DatabaseException;

}
