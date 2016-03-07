package base;

import base.datasets.UserDataSet;

import java.util.List;

public interface DBService {
    String getLocalStatus();

    boolean saveUser (UserDataSet dataSet);

    UserDataSet getUserById (long id);

    UserDataSet getUserByEmail(String email);

    UserDataSet getUserByLogin(String login);

    boolean updateUserEmail(Long id, String email, String login, String pass);

    boolean deleteUserById(Long id);

    List<UserDataSet> getAllUsers();

    void shutdown();
}
