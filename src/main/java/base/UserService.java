package base;


import base.datasets.UserDataSet;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import dbservice.DatabaseException;
import org.hibernate.Session;

import java.util.List;

public interface UserService {
    void saveUser (UserDataSet dataSet) throws DatabaseException;

    UserDataSet getUserById (long id) throws DatabaseException;

    UserDataSet getUserByEmail(String email) throws DatabaseException;

    UserDataSet getUserByLogin(String login) throws DatabaseException;

    boolean deleteUserById(Long id) throws DatabaseException;

    List<UserDataSet> getUsers(boolean allUsers) throws DatabaseException;

    boolean isEmailUnique(String email) throws DatabaseException;

    boolean isLoginUnique(String login) throws DatabaseException;

}
