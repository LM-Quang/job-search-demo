package com.service;

import com.entity.*;

import java.util.List;

public interface UserService {
    void sendConfirmEmail(int userId);
    void addSaveJob(int userId, int recruitmentId);
    void addFollowCompany(int userId, int companyId);
    void addCV(String cvName);
    void deleteSaveJob(int id);
    void deleteSaveJob(int userId, int recruitmentId);
    void deleteApplyPost(int id);
    void deleteFollowCompany(int id);
    void deleteCV(int id);
    int getApplyPosts(List<Recruitment> recruitments);
    int getNumOfSaveJobs(int userId);
    int getNumOfApplyPosts(int userId);
    int getNumOfFollowCompanies(int userId);
    int getNumOfRecruitments(int companyId);
    boolean confirmAccount(int userId);
    boolean updateCompany(int companyId, String email, String name, String address,
                          String phoneNumber, String description);
    boolean isEmailExisted(String email);
    boolean isEmailExisted(int id, String email);
    boolean updateUser(int userId, String email, String fullName, String address, String phoneNumber, String description);
    boolean addRecruitment(String title, String description, String experience,int quantity, String address,
                           String deadline,String salary, int typeId, int categoryId, int userId);
    boolean updateRecruitment(int recruitmentId, String title, String description, String experience,int quantity,
                              String address, String deadline,String salary, int typeId, int categoryId);
    boolean deleteRecruitmentById(int recruitmentId);
    boolean deleteUserCV(int userId);
    boolean addApplyPost(String nameCv, String text, int userId, int recruitmentId);
    boolean isSaveJobExisted(int userId, int recruitmentId);
    boolean isFollowCompanyExisted(int userId, int companyId);
    boolean isApplyPostExisted(int userId, int recruitmentId);
    boolean updateCV(int userId, String newCV);
    List<JobType> getJobTypes();
    List<Category> getCategories();
    User getUserById(int id);
    User getUserByEmail(String email);
    Company getCompanyById(int id);
    Company getCompanyByUserId(int userId);
    Recruitment getRecruitmentById(int id);
    List<Recruitment> getRelatedRecruitments(int recruitmentId, int companyId, int categoryId);
    List<Recruitment> getRecruitmentsByCompanyId(int companyID, int pageNum);
    List<ApplyPost> getApplyPostsByRecruitmentId(int recruitmentId);
    List<ApplyPost> getApplyPostsByUserId(int userId, int pageNum);
    List<ApplyPost> getApplyPostsFromRecruitment(List<Recruitment> recruitments, int pageNum);
    List<SaveJob> getSaveJobsByUserId(int userId, int pageNum);
    List<FollowCompany> getFollowCompaniesByUserId(int userId, int pageNum);
    CV getCVById(int cvId);
}
