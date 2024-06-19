package com.dao;

import com.entity.Company;

public interface CompanyDAO {
    long getNumOfCompanies();
    Company getCompanyById(int id);
    Company getCompanyByUserId(int userId);
    Company getCompanyByName(String name);
    void addCompany(Company newCompany);
    boolean updateCompany(Company updateCompany);
}
