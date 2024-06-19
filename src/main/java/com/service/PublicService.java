package com.service;

import com.entity.*;

import java.util.List;

public interface PublicService {
    long getNumOfApplicants();
    long getNumOfCompanies();
    long getNumOfRecruitments();
    long getNumOfRecruitmentsBySearch(String searchType, String keyword);
    List<Category> getCategories();
    List<Recruitment> getRecruitments();
    List<Recruitment> getRecruitments(int pageNum);
    List<Recruitment> getRecruitments(String searchType, String keyword, int pageNum);
    List<Recruitment> getTopRecruitments();
    Company getTopCompany();
    User getUserById(int id);
}
