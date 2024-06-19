package com.dao;

import com.entity.ApplyPost;
import com.entity.Recruitment;
import com.helper.Helper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApplyPostDAOImpl implements ApplyPostDAO{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Recruitment getRecruitmentWithMostApply() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT recruitment FROM ApplyPost GROUP BY recruitment_id ORDER BY COUNT(user_id) DESC";
        return session.createQuery(hql, Recruitment.class)
                .setFirstResult(0)
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    public List<ApplyPost> getApplyPostsByRecruitmentId(int recruitmentId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM ApplyPost WHERE recruitment_id = :id ORDER BY status";
        return session.createQuery(hql, ApplyPost.class)
                .setParameter("id", recruitmentId)
                .getResultList();
    }

    @Override
    public boolean isRecruitmentApplied(int recruitmentId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT (user_id) FROM ApplyPost WHERE recruitment_id = :id";
        long result = (long) session.createQuery(hql).setParameter("id", recruitmentId).uniqueResult();
        return result != 0;
    }

    @Override
    public List<ApplyPost> getApplyPostsFromRecruitmentIds(List<Integer> recruitmentIds, int pageNum) {
        int start = (pageNum - 1) * Helper.APPLY_POST_PER_PAGE;
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM ApplyPost WHERE recruitment_id IN (:list) ORDER BY status, recruitment_id";
        return session.createQuery(hql, ApplyPost.class)
                .setParameter("list", recruitmentIds)
                .setFirstResult(start)
                .setMaxResults(Helper.APPLY_POST_PER_PAGE)
                .getResultList();
    }

    @Override
    public long getApplyPosts(List<Integer> recruitmentIds) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM ApplyPost WHERE recruitment_id IN (:list)";
        return (long) session.createQuery(hql)
                .setParameter("list", recruitmentIds)
                .getSingleResult();
    }

    @Override
    public boolean addApplyPost(ApplyPost newApplyPost) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.save(newApplyPost);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isApplyPostExisted(int userId, int recruitmentId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM ApplyPost WHERE user_id = :userId AND recruitment_id = :recruitmentId";
        ApplyPost applyPost =
                session.createQuery(hql,ApplyPost.class)
                        .setParameter("userId", userId)
                        .setParameter("recruitmentId", recruitmentId)
                        .uniqueResult();
        return applyPost != null;
    }

    @Override
    public List<ApplyPost> getApplyPostByUserId(int userId, int pageNum) {
        int start = (pageNum - 1) * Helper.APPLY_POST_PER_PAGE;
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM ApplyPost WHERE user_id = :userId";
        return session.createQuery(hql, ApplyPost.class)
                .setParameter("userId", userId)
                .setFirstResult(start)
                .setMaxResults(Helper.APPLY_POST_PER_PAGE)
                .getResultList();
    }

    @Override
    public long getApplyPosts(int userId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT (*) FROM ApplyPost WHERE user_id = :userId";
        return (long) session.createQuery(hql)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    public void deleteApplyPost(int id) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "DELETE FROM ApplyPost WHERE id = :id";
        session.createQuery(hql)
                .setParameter("id", id)
                .executeUpdate();
    }
}
