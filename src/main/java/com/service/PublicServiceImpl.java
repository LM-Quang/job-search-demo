package com.service;

import com.dao.*;
import com.entity.Category;
import com.entity.Company;
import com.entity.Recruitment;
import com.entity.User;
import com.helper.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PublicServiceImpl implements PublicService {
    private final UserDAO userDAO;
    private final CompanyDAO companyDAO;
    private final RecruitmentDAO recruitmentDAO;
    private final CategoryDAO categoryDAO;
    private final ApplyPostDAO applyPostDAO;

    @Autowired
    public PublicServiceImpl(UserDAO userDAO, CompanyDAO companyDAO,
                             RecruitmentDAO recruitmentDAO, CategoryDAO categoryDAO,
                             ApplyPostDAO applyPostDAO) {
        this.userDAO = userDAO;
        this.companyDAO = companyDAO;
        this.recruitmentDAO = recruitmentDAO;
        this.categoryDAO = categoryDAO;
        this.applyPostDAO = applyPostDAO;
    }

    @Override
    public long getNumOfApplicants() {
        return userDAO.getNumOfApplicants();
    }

    @Override
    public long getNumOfCompanies() {
        return companyDAO.getNumOfCompanies();
    }

    @Override
    public long getNumOfRecruitments() {
        return recruitmentDAO.getNumOfRecruitments();
    }

    @Override
    public List<Category> getCategories() {
        return categoryDAO.getCategories();
    }

    @Override
    public List<Recruitment> getRecruitments() {
        return recruitmentDAO.getRecruitments();
    }

    @Override
    public long getNumOfRecruitmentsBySearch(String searchType, String keyword) {
        if (keyword.equals(Helper.EMPTY)) {
            return recruitmentDAO.getNumOfRecruitments();
        }

        if (searchType.equals(Helper.SEARCH_BY_NAME)) {
            return recruitmentDAO.getNumOfRecruitmentsByTitle(keyword);
        } else if (searchType.equals(Helper.SEARCH_BY_COMPANY)) {
            Company company = companyDAO.getCompanyByName(keyword);
            return company.getRecruitments().size();
        } else {
            return recruitmentDAO.getNumOfRecruitmentsByLocation(keyword);
        }
    }

    @Override
    public List<Recruitment> getRecruitments(int pageNum) {
        return recruitmentDAO.getRecruitments(pageNum);
    }

    @Override
    public List<Recruitment> getRecruitments(String searchType, String keyword, int pageNum) {
        if (keyword.equals(Helper.EMPTY)) {
            return recruitmentDAO.getRecruitments(pageNum);
        }

        if (searchType.equals(Helper.SEARCH_BY_NAME)) {
            return recruitmentDAO.getRecruitmentsByTitle(keyword, pageNum);
        } else if (searchType.equals(Helper.SEARCH_BY_COMPANY)) {
            Company company = companyDAO.getCompanyByName(keyword);
            return recruitmentDAO.getRecruitmentsByCompanyId(company.getId(), pageNum);
        } else {
            return recruitmentDAO.getRecruitmentsByLocation(keyword, pageNum);
        }
    }

    @Override
    public List<Recruitment> getTopRecruitments() {
        Recruitment recruitment1 = applyPostDAO.getRecruitmentWithMostApply();
        Recruitment recruitment2 = recruitmentDAO.getRecruitmentWithHighestSalary();

        List<Recruitment> recruitments = new ArrayList<>();
        recruitments.add(recruitment1);
        recruitments.add(recruitment2);
        return recruitments;
    }

    @Override
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    @Override
    public Company getTopCompany() {
        Recruitment recruitment = applyPostDAO.getRecruitmentWithMostApply();
        return recruitment.getCompany();
    }
}
