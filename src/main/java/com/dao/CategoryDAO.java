package com.dao;

import com.entity.Category;

import java.util.List;

public interface CategoryDAO {
    Category getCategoryById(int id);
    List<Category> getCategories();
}
