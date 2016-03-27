package base;


import base.datasets.UserDataSet;
import dbservice.DatabaseException;

import java.util.List;

public interface UserService {
    long saveUser (UserDataSet dataSet) throws DatabaseException;

    UserDataSet getUserById (long id) throws DatabaseException;

    UserDataSet getUserByEmail(String email) throws DatabaseException;

    UserDataSet getUserByLogin(String login) throws DatabaseException;

    boolean updateUserInfo(Long id, String email, String login, String pass) throws DatabaseException;

    boolean deleteUserById(Long id) throws DatabaseException;

    List<UserDataSet> getAllUsers() throws DatabaseException;
}
