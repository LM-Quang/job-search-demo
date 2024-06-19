package com.dao;

import com.entity.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDAOImpl  implements CategoryDAO{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Category getCategoryById(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Category.class, id);
    }

    @Override
    public List<Category> getCategories() {
        Session session = sessionFactory.getCurrentSession();
        return session
                .createQuery("From Category ORDER BY numberChoose DESC", Category.class)
                .setFirstResult(0)
                .setMaxResults(4)
                .getResultList();
    }
}
