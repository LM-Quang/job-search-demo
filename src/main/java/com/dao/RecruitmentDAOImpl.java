package com.dao;

import com.entity.Recruitment;
import com.helper.Helper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class RecruitmentDAOImpl implements RecruitmentDAO{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public long getNumOfRecruitments() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Recruitment";
        return (long) session.createQuery(hql).getSingleResult();
    }

    @Override
    public long getNumOfRecruitmentsByTitle(String keyword) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Recruitment WHERE title LIKE :keyword";
        return (long) session.createQuery(hql).setParameter("keyword", "%" + keyword + "%").getSingleResult();
    }

    @Override
    public long getNumOfRecruitmentsByLocation(String keyword) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Recruitment WHERE address LIKE :keyword";
        return (long) session.createQuery(hql).setParameter("keyword", "%" + keyword + "%").getSingleResult();
    }

    @Transactional
    @Override
    public Recruitment getRecruitmentWithHighestSalary() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Recruitment ORDER BY salary DESC";
        return session.createQuery(hql, Recruitment.class)
                .setFirstResult(0)
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    public Recruitment getRecruitmentById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Recruitment.class, id);
    }

    @Override
    public List<Recruitment> getRecruitments() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("From Recruitment", Recruitment.class).getResultList();
    }

    @Override
    public List<Recruitment> getRecruitments(int pageNum) {
        int start = (pageNum - 1) * Helper.RECRUITMENT_PER_PAGE;
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("From Recruitment", Recruitment.class)
                .setFirstResult(start)
                .setMaxResults(Helper.RECRUITMENT_PER_PAGE)
                .getResultList();
    }

    @Override
    public List<Recruitment> getRecruitmentsByTitle(String keyword, int pageNum) {
        int start = (pageNum - 1) * Helper.RECRUITMENT_PER_PAGE;
        Session session = sessionFactory.getCurrentSession();
        String hql = "From Recruitment WHERE title LIKE :keyword";
        return session.createQuery(hql, Recruitment.class)
                .setParameter("keyword", "%" + keyword + "%")
                .setFirstResult(start)
                .setMaxResults(Helper.RECRUITMENT_PER_PAGE)
                .getResultList();
    }

    @Override
    public List<Recruitment> getRecruitmentsByCompanyId(int companyId, int pageNum) {
        int start = (pageNum - 1) * Helper.RECRUITMENT_PER_PAGE;
        Session session = sessionFactory.getCurrentSession();
        String hql = "From Recruitment WHERE company_id = :id";
        return session
                .createQuery(hql, Recruitment.class)
                .setParameter("id", companyId)
                .setFirstResult(start)
                .setMaxResults(Helper.RECRUITMENT_PER_PAGE)
                .getResultList();
    }

    @Override
    public List<Recruitment> getRecruitmentsByLocation(String keyword, int pageNum) {
        int start = (pageNum - 1) * Helper.RECRUITMENT_PER_PAGE;
        Session session = sessionFactory.getCurrentSession();
        String hql = "From Recruitment WHERE address LIKE :keyword";
        return session
                .createQuery(hql, Recruitment.class)
                .setParameter("keyword", "%" + keyword + "%")
                .setFirstResult(start)
                .setMaxResults(Helper.RECRUITMENT_PER_PAGE)
                .getResultList();
    }

    @Override
    public List<Recruitment> getRelatedRecruitments(int recruitmentId, int companyId, int categoryId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Recruitment WHERE id != :recruitmentId AND company_id = :companyId AND category_id = :categoryId";
        return session.createQuery(hql, Recruitment.class)
                .setParameter("recruitmentId", recruitmentId)
                .setParameter("companyId", companyId)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public boolean addRecruitment(Recruitment newRecruitment) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.save(newRecruitment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateRecruitment(Recruitment updateRecruitment) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.update(updateRecruitment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteRecruitmentById(int id) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "DELETE FROM Recruitment WHERE id = :id";
        try {
            session.createQuery(hql).setParameter("id", id).executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getNumOfRecruitmentsByCompanyId(int companyId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Recruitment WHERE company_id = :companyId";
        return (long) session.createQuery(hql)
                .setParameter("companyId", companyId)
                .getSingleResult();
    }
}
