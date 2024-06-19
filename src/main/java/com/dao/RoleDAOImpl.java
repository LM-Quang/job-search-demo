package com.dao;

import com.entity.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDAOImpl implements RoleDAO{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Role> getRoles() {
        Session session = sessionFactory.getCurrentSession();
        Query<Role> query = session.createQuery("FROM Role", Role.class);
        return query.getResultList();
    }
}
