package com.service;

import com.entity.User;

public interface AuthenticationService {
    User getUserByEmail(String email);
    boolean addUser(String email, String fullName, String password, int roleID);
    boolean isEmailExisted(String email);
    void addCompany(User user);
}
