package com.dao;

import com.entity.FollowCompany;

import java.util.List;

public interface FollowCompanyDAO {
    void addFollowCompany(FollowCompany newFollowCompany);
    void deleteFollowCompany(int id);
    long getFollowCompanies(int userId);
    boolean isExisted(int userId, int companyId);
    List<FollowCompany> getFollowCompanies(int userId, int pageNum);
}
