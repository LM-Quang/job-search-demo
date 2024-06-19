package com.dao;

import com.entity.CV;
import com.entity.User;

public interface UserDAO {
    long getNumOfApplicants();
    User getUserById(int id);
    User getUserByEmail(String email);
    boolean addUser(User newUser);
    boolean isEmailExisted(String email);
    boolean isEmailExisted(int id, String email);
    boolean updateUser(User updateUser);
    boolean deleteCV(int userID);
    void updateStatus(int userId, int newStatus);
    boolean updateCV(User user, CV newCV);
}
