package com.dao;

import com.entity.SaveJob;
import com.helper.Helper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SaveJobDAOImpl implements SaveJobDAO {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addSaveJob(SaveJob newSaveJob) {
        Session session = sessionFactory.getCurrentSession();
        session.save(newSaveJob);
    }

    @Override
    public void deleteSaveJob(int id) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "DELETE FROM SaveJob WHERE id = :id";
        session.createQuery(hql)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void deleteSaveJob(int userId, int recruitmentId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "DELETE FROM SaveJob WHERE user_id = :userId AND recruitment_id = :recruitmentId";
        session.createQuery(hql)
                .setParameter("userId", userId)
                .setParameter("recruitmentId", recruitmentId)
                .executeUpdate();
    }

    @Override
    public boolean isExisted(int userId, int recruitmentId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SaveJob WHERE user_id = :userId AND recruitment_id = :recruitmentId";
        SaveJob saveJob =
                session.createQuery(hql, SaveJob.class)
                        .setParameter("userId", userId)
                        .setParameter("recruitmentId", recruitmentId)
                        .uniqueResult();
        return saveJob != null;
    }

    @Override
    public long getNumOfSaveJobs(int userId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT (*) FROM SaveJob WHERE user_id = :userId";
        return (long) session.createQuery(hql).setParameter("userId", userId).getSingleResult();
    }

    @Override
    public List<SaveJob> getSaveJobsByUserId(int userId, int pageNum) {
        int start = (pageNum - 1) * Helper.SAVE_JOB_PER_PAGE;
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SaveJob WHERE user_id = :userId";
        return session.createQuery(hql, SaveJob.class)
                .setParameter("userId", userId)
                .setFirstResult(start)
                .setMaxResults(Helper.SAVE_JOB_PER_PAGE)
                .getResultList();
    }
}
