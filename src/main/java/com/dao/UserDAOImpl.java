package com.dao;

import com.entity.CV;
import com.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public long getNumOfApplicants() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM User WHERE role_id = " + 1;
        return (long) session.createQuery(hql).getSingleResult();
    }

    @Override
    public User getUserById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(User.class, id);
    }

    @Override
    public User getUserByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM User WHERE email = :email";
        return session.createQuery(hql, User.class).setParameter("email", email).uniqueResult();
    }

    @Override
    public boolean addUser(User newUser) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.save(newUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isEmailExisted(String email) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM User WHERE email = :email";
        User user = session.createQuery(hql, User.class)
                .setParameter("email", email)
                .uniqueResult();

        return user != null;
    }

    @Override
    public boolean isEmailExisted(int id, String email) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM User WHERE id != :id AND email = :email";
        User user =  session.createQuery(hql, User.class)
                .setParameter("id", id)
                .setParameter("email",email)
                .uniqueResult();

        return user != null;
    }

    @Override
    public boolean updateUser(User updateUser) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.update(updateUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteCV(int userID) {
        Session session = sessionFactory.getCurrentSession();
        try {
            User user = session.get(User.class, userID);
            user.setCv(null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void updateStatus(int userId, int newStatus) {
        Session session = sessionFactory.getCurrentSession();
        User user = session.get(User.class, userId);
        user.setStatus(newStatus);
    }

    @Override
    public boolean updateCV(User user, CV newCV) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "";
        return false;
    }
}
