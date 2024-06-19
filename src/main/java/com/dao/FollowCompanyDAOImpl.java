package com.dao;

import com.entity.FollowCompany;
import com.helper.Helper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FollowCompanyDAOImpl implements FollowCompanyDAO{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean isExisted(int userId, int companyId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM FollowCompany WHERE user_id = :userId AND company_id = :companyId";
        FollowCompany followCompany =
                session.createQuery(hql, FollowCompany.class)
                        .setParameter("userId", userId)
                        .setParameter("companyId", companyId)
                        .uniqueResult();
        return followCompany != null;
    }

    @Override
    public void addFollowCompany(FollowCompany newFollowCompany) {
        Session session = sessionFactory.getCurrentSession();
        session.save(newFollowCompany);
    }

    @Override
    public void deleteFollowCompany(int id) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "DELETE FROM FollowCompany WHERE id = :id";
        session.createQuery(hql)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public long getFollowCompanies(int userId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM FollowCompany WHERE user_id = :userId";
        return (long) session.createQuery(hql).setParameter("userId", userId).getSingleResult();
    }

    @Override
    public List<FollowCompany> getFollowCompanies(int userId, int pageNum) {
        int start = (pageNum - 1) * Helper.FOLLOW_COMPANY_PER_PAGE;
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM FollowCompany WHERE user_id = :userId";
        return session.createQuery(hql, FollowCompany.class)
                .setParameter("userId", userId)
                .setFirstResult(start)
                .setMaxResults(Helper.FOLLOW_COMPANY_PER_PAGE)
                .getResultList();
    }
}
