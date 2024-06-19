package com.dao;

import com.entity.CV;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CVDAOImpl implements CVDAO{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addCV(CV newCv) {
        Session session = sessionFactory.getCurrentSession();
        session.save(newCv);
    }

    @Override
    public boolean updateCV(int id, String newName) {
        Session session = sessionFactory.getCurrentSession();
        try {
            CV cv = session.get(CV.class, id);
            cv.setFileName(newName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteCV(int id) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "DELETE FROM CV where id = :id";
        session.createQuery(hql).setParameter("id", id).executeUpdate();
    }

    @Override
    public CV getCVById(int id) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM CV WHERE id = :id";
        return session.createQuery(hql, CV.class).setParameter("id", id).uniqueResult();
    }
}
