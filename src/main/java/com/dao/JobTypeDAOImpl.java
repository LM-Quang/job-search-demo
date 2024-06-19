package com.dao;

import com.entity.JobType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JobTypeDAOImpl implements JobTypeDAO{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public JobType getJobTypeById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(JobType.class, id);
    }

    @Override
    public List<JobType> getJobTypes() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("From JobType", JobType.class).getResultList();
    }
}
