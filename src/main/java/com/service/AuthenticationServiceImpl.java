package com.service;

import com.dao.CompanyDAO;
import com.dao.UserDAO;
import com.entity.Company;
import com.entity.Role;
import com.entity.User;
import com.helper.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{
    private final UserDAO userDAO;
    private final CompanyDAO companyDAO;

    @Autowired
    public AuthenticationServiceImpl(UserDAO userDAO, CompanyDAO companyDAO) {
        this.userDAO = userDAO;
        this.companyDAO = companyDAO;
    }

    @Override
    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    @Transactional
    @Override
    public boolean addUser(String email, String fullName, String password, int roleID) {
        Role role = new Role();
        if (roleID == Helper.ROLE_APPLICANT_ID) {
            role.setId(Helper.ROLE_APPLICANT_ID);
            role.setRoleName(Helper.ROLE_APPLICANT);
        } else {
            role.setId(Helper.ROLE_HR_ID);
            role.setRoleName(Helper.ROLE_HR);
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setPassword(password);
        newUser.setRole(role);
        newUser.setStatus(Helper.STATUS_NOT_CONFIRMED);

        return userDAO.addUser(newUser);
    }

    @Override
    public boolean isEmailExisted(String email) {
        return userDAO.isEmailExisted(email);
    }

    @Transactional
    @Override
    public void addCompany(User user) {
        Company company = new Company();
        company.setStatus(Helper.STATUS_ACTIVE);
        company.setUser(user);

        companyDAO.addCompany(company);
    }
}
