package com.dao;

import com.entity.Company;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyDAOImpl implements CompanyDAO{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public long getNumOfCompanies() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(*) FROM Company";
        return (long) session.createQuery(hql).getSingleResult();
    }

    @Override
    public Company getCompanyById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Company.class, id);
    }

    @Override
    public Company getCompanyByUserId(int userId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Company WHERE user_id = :id";
        return session.createQuery(hql, Company.class).setParameter("id", userId).uniqueResult();
    }

    @Override
    public Company getCompanyByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Company WHERE companyName LIKE :name";
        return session.createQuery(hql, Company.class).setParameter("name", "%" + name + "%").getSingleResult();
    }

    @Override
    public void addCompany(Company newCompany) {
        Session session = sessionFactory.getCurrentSession();
        session.save(newCompany);
    }

    @Override
    public boolean updateCompany(Company updateCompany) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.update(updateCompany);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
