package com.dao;

import com.entity.Recruitment;

import java.util.List;

public interface RecruitmentDAO {
    boolean addRecruitment(Recruitment newRecruitment);
    boolean updateRecruitment(Recruitment updateRecruitment);
    boolean deleteRecruitmentById(int id);
    long getNumOfRecruitments();
    long getNumOfRecruitmentsByCompanyId(int companyId);
    long getNumOfRecruitmentsByTitle(String keyword);
    long getNumOfRecruitmentsByLocation(String keyword);
    Recruitment getRecruitmentWithHighestSalary();
    Recruitment getRecruitmentById(int id);
    List<Recruitment> getRecruitments();
    List<Recruitment> getRecruitments(int pageNum);
    List<Recruitment> getRecruitmentsByTitle(String keyword, int pageNum);
    List<Recruitment> getRecruitmentsByCompanyId(int companyId, int pageNum);
    List<Recruitment> getRecruitmentsByLocation(String keyword, int pageNum);
    List<Recruitment> getRelatedRecruitments(int recruitmentId, int companyId, int categoryId);
}
