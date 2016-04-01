package dbservice.dao;

import base.datasets.UserDataSet;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.LockMode;

import java.util.List;

public class UserDataSetDAO {
    private Session session;

    public UserDataSetDAO(Session session) {
        this.session = session;
    }

    public void save(UserDataSet dataSet) {
        session.save(dataSet);
    }

    public UserDataSet readById (long id) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria
                .add(Restrictions.eq("id", id))
                .add(Restrictions.eq("isDeleted", false))
                .uniqueResult();
    }

    public UserDataSet readByEmail(String email) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria
                .add(Restrictions.eq("email", email))
                .add(Restrictions.eq("isDeleted", false))
                .uniqueResult();
    }

    @SuppressWarnings("JpaQlInspection")
    public boolean markAsDeletedById(Long id) {
        final int affected = session.createQuery("UPDATE UserDataSet a SET a.isDeleted= :del WHERE a.id = :id ")
                .setParameter("del", true)
                .setParameter("id", id)
                .executeUpdate();
        return affected == 1;
    }

    @SuppressWarnings("JpaQlInspection")
    public boolean updateUserInfo(Long id, String login, String passw) {
        if ( !checkUniqueLogin(login) ){
            return false;
        }
        final int affected = session.createQuery("UPDATE UserDataSet a SET " +
                "a.login = :log, a.password = :pass WHERE a.id = :id AND a.isDeleted = :isDel")
                .setParameter("log", login)
                .setParameter("pass", passw)
                .setParameter("id", id)
                .setParameter("isDel", false)
                .executeUpdate();
        return affected == 1;
    }

    public UserDataSet readByLogin(String login) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria
                .add(Restrictions.eq("login", login))
                .add(Restrictions.eq("isDeleted", false))
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<UserDataSet> readAll(boolean skipDeleted) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        if (skipDeleted) {
            criteria.add(Restrictions.eq("isDeleted", false));
        }
        return (List<UserDataSet>) criteria.list();
    }

    public boolean checkUniqueLogin(String login) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        final UserDataSet userExist =  (UserDataSet) criteria.setLockMode(LockMode.PESSIMISTIC_WRITE)
                .add(Restrictions.eq("login", login))
                .uniqueResult();
        return userExist == null;
    }

    public boolean checkUniqueEmail(String email) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        final UserDataSet userExist =  (UserDataSet) criteria.setLockMode(LockMode.PESSIMISTIC_WRITE)
                .add(Restrictions.eq("email", email))
                .uniqueResult();
        return userExist == null;
    }
}
