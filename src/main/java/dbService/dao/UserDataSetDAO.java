package dbservice.dao;

import base.datasets.UserDataSet;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.LockMode;

import java.util.List;

public class UserDataSetDAO {
    private final Session session;

    public UserDataSetDAO(Session session) {
        this.session = session;
    }

    public Long save(UserDataSet dataSet) {
        return (Long) session.save(dataSet);
    }

    public UserDataSet readById(long id) {
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
                .add(Restrictions.eq("isAnonymous", false))
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

    public UserDataSet readByLogin(String login) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria
                .add(Restrictions.eq("login", login))
                .add(Restrictions.eq("isDeleted", false))
                .add(Restrictions.eq("isAnonymous", false))
                .uniqueResult();
    }

    @SuppressWarnings("JpaQlInspection")
    public void incrementScore(long id) {

        session.createQuery("UPDATE UserDataSet a SET " +
                "a.score = a.score + 1 WHERE a.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<UserDataSet> getTopTen() {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        criteria.add(Restrictions.eq("isDeleted", false))
                .addOrder(Order.desc("score"))
                .setMaxResults(10);
        return (List<UserDataSet>) criteria.list();
    }

    public boolean checkUniqueLogin(String login) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        final UserDataSet userExist = (UserDataSet) criteria.setLockMode(LockMode.PESSIMISTIC_WRITE)
                .add(Restrictions.eq("login", login))
                .add(Restrictions.eq("isAnonymous", false))
                .uniqueResult();
        return userExist == null;
    }

    public boolean checkUniqueEmail(String email) {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        final UserDataSet userExist = (UserDataSet) criteria.setLockMode(LockMode.PESSIMISTIC_WRITE)
                .add(Restrictions.eq("email", email))
                .uniqueResult();
        return userExist == null;
    }
}
