package dbservice.dao;

import base.datasets.UserDataSet;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.LockMode;

import java.util.List;

public class UserDataSetDAO {
    private Session session;

    public UserDataSetDAO(Session session) {
        this.session = session;
    }

    public long save(UserDataSet dataSet) throws ConstraintViolationException  {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        final UserDataSet ifExists =  (UserDataSet) criteria.setLockMode(LockMode.PESSIMISTIC_WRITE)
                .add(Restrictions.eq("login", dataSet.getLogin())).uniqueResult();
        if (ifExists != null){
            return -1;
        }
        session.save(dataSet);
        return dataSet.getId();
    }

    public UserDataSet readById (long id) {
        return session.get(UserDataSet.class, id);
    }

    public UserDataSet readByEmail(String email) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("email", email)).uniqueResult();
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
    public boolean updateUserInfo(Long id, String email, String login, String passw) {
        final int affected =session.createQuery("UPDATE UserDataSet a SET a.email= :emailNew," +
                " a.login = :log, a.password = :pass")
                .setParameter("emailNew",email)
                .setParameter("log", login)
                .setParameter("pass", passw)
                .executeUpdate();
        return affected == 1;
    }

    public UserDataSet readByLogin(String login) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("login", login)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<UserDataSet> readAll() {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (List<UserDataSet>) criteria.list();
    }

}
