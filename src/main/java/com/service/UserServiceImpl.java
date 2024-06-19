package com.service;

import com.dao.*;
import com.entity.*;
import com.helper.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    private final UserDAO userDAO;
    private final CompanyDAO companyDAO;
    private final RecruitmentDAO recruitmentDAO;
    private final ApplyPostDAO applyPostDAO;
    private final CategoryDAO categoryDAO;
    private final JobTypeDAO jobTypeDAO;
    private final SaveJobDAO saveJobDAO;
    private final FollowCompanyDAO followCompanyDAO;
    private final CVDAO cvDAO;
    private final JavaMailSender mailSender;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, CompanyDAO companyDAO, RecruitmentDAO recruitmentDAO,
                         CategoryDAO categoryDAO, JobTypeDAO jobTypeDAO, ApplyPostDAO applyPostDAO,
                          SaveJobDAO saveJobDAO, FollowCompanyDAO followCompanyDAO, CVDAO cvDAO,
                            JavaMailSender mailSender) {
        this.userDAO = userDAO;
        this.companyDAO = companyDAO;
        this.recruitmentDAO = recruitmentDAO;
        this.categoryDAO = categoryDAO;
        this.jobTypeDAO = jobTypeDAO;
        this.applyPostDAO = applyPostDAO;
        this.saveJobDAO = saveJobDAO;
        this.followCompanyDAO = followCompanyDAO;
        this.cvDAO = cvDAO;
        this.mailSender = mailSender;
    }

    @Transactional
    @Override
    public void sendConfirmEmail(int userId) {
        User user = userDAO.getUserById(userId);
        String msg = "Click here http://localhost:8080/ASM2/user/processConfirm/" + user.getId();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("learningspring@outlook.com");
        message.setTo(user.getEmail());
        message.setSubject("Confirm Email");
        message.setText(msg);
        mailSender.send(message);

        // Chuyển từ trạng thái Chưa xác nhận => Chờ xác nhận
        user.setStatus(Helper.STATUS_CONFIRMING);
        userDAO.updateUser(user);
    }

    @Transactional
    @Override
    public boolean confirmAccount(int userId) {
        User user = userDAO.getUserById(userId);
        user.setStatus(Helper.STATUS_CONFIRMED);
        return userDAO.updateUser(user);
    }

    @Override
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    @Override
    public Company getCompanyByUserId(int userId) {
        return companyDAO.getCompanyByUserId(userId);
    }

    @Override
    public Recruitment getRecruitmentById(int id) {
        return recruitmentDAO.getRecruitmentById(id);
    }

    @Override
    public List<Recruitment> getRecruitmentsByCompanyId(int companyId, int pageNum) {
        return recruitmentDAO.getRecruitmentsByCompanyId(companyId, pageNum);
    }

    @Override
    public List<Recruitment> getRelatedRecruitments(int recruitmentId, int companyId, int categoryId) {
        return recruitmentDAO.getRelatedRecruitments(recruitmentId, companyId, categoryId);
    }

    @Override
    public List<ApplyPost> getApplyPostsByRecruitmentId(int recruitmentId) {
        return applyPostDAO.getApplyPostsByRecruitmentId(recruitmentId);
    }

    @Override
    public List<ApplyPost> getApplyPostsFromRecruitment(List<Recruitment> recruitments, int pageNum) {
        List<Integer> recruitmentIds = new ArrayList<>();
        recruitments.forEach(recruitment -> recruitmentIds.add(recruitment.getId()));
        return applyPostDAO.getApplyPostsFromRecruitmentIds(recruitmentIds, pageNum);
    }

    @Transactional
    @Override
    public boolean updateCompany(int companyId, String email, String name, String address,
                                 String phoneNumber, String description) {
        Company company = companyDAO.getCompanyById(companyId);
        company.setEmail(email);
        company.setCompanyName(name);
        company.setAddress(address);
        company.setPhoneNumber(phoneNumber);
        company.setDescription(description);
        return companyDAO.updateCompany(company);
    }

    @Override
    public boolean isEmailExisted(String email) {
        return userDAO.isEmailExisted(email);
    }

    @Override
    public boolean isEmailExisted(int id, String email) {
        return userDAO.isEmailExisted(id, email);
    }

    @Transactional
    @Override
    public boolean updateUser(int userId, String email, String fullName, String address,
                              String phoneNumber, String description) {
        User user = userDAO.getUserById(userId);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        user.setDescription(description);
        return userDAO.updateUser(user);
    }

    @Override
    public List<Category> getCategories() {
        return categoryDAO.getCategories();
    }

    @Override
    public List<JobType> getJobTypes() {
        return jobTypeDAO.getJobTypes();
    }

    @Transactional
    @Override
    public boolean addRecruitment(String title, String description, String experience, int quantity,
                                  String address, String deadline, String salary, int typeId, int categoryId, int userId) {
        JobType type = jobTypeDAO.getJobTypeById(typeId);
        Category category = categoryDAO.getCategoryById(categoryId);
        Company company = companyDAO.getCompanyByUserId(userId);

        Recruitment newRecruitment = new Recruitment(address, LocalDate.now().toString(), description, experience,
                quantity, salary, Helper.STATUS_ACTIVE, title, deadline, type, category, company);
        return recruitmentDAO.addRecruitment(newRecruitment);
    }

    @Transactional
    @Override
    public boolean updateRecruitment(int recruitmentId, String title, String description, String experience,
                                     int quantity, String address, String deadline, String salary, int typeId, int categoryId) {
        JobType type = jobTypeDAO.getJobTypeById(typeId);
        Category category = categoryDAO.getCategoryById(categoryId);

        Recruitment recruitment = recruitmentDAO.getRecruitmentById(recruitmentId);
        recruitment.setTitle(title);
        recruitment.setDescription(description);
        recruitment.setExperience(experience);
        recruitment.setQuantity(quantity);
        recruitment.setAddress(address);
        recruitment.setDeadline(deadline);
        recruitment.setSalary(salary);
        recruitment.setJobType(type);
        recruitment.setCategory(category);

        return recruitmentDAO.updateRecruitment(recruitment);
    }

    @Transactional
    @Override
    public boolean deleteRecruitmentById(int recruitmentId) {
        boolean isApplied = applyPostDAO.isRecruitmentApplied(recruitmentId);
        if (!isApplied) {
            return recruitmentDAO.deleteRecruitmentById(recruitmentId);
        }
        return false;
    }

    @Override
    public int getApplyPosts(List<Recruitment> recruitments) {
        List<Integer> recruitmentIds = new ArrayList<>();
        recruitments.forEach(recruitment -> recruitmentIds.add(recruitment.getId()));
        return (int) applyPostDAO.getApplyPosts(recruitmentIds);
    }

    @Transactional
    @Override
    public boolean deleteUserCV(int userId) {
        User user = userDAO.getUserById(userId);
        user.setCv(null);
        return userDAO.updateUser(user);
    }

    @Transactional
    @Override
    public void deleteCV(int id) {
        cvDAO.deleteCV(id);
    }

    @Transactional
    @Override
    public boolean addApplyPost(String nameCv, String text, int userId, int recruitmentId) {
        User user = userDAO.getUserById(userId);
        Recruitment recruitment = recruitmentDAO.getRecruitmentById(recruitmentId);

        if (!applyPostDAO.isApplyPostExisted(userId, recruitmentId)) {
            ApplyPost newApplyPost =
                    new ApplyPost(LocalDate.now().toString(), nameCv, Helper.STATUS_ACTIVE, text, user, recruitment);
            return applyPostDAO.addApplyPost(newApplyPost);
        }
        return false;
    }

    @Override
    public boolean isSaveJobExisted(int userId, int recruitmentId) {
        return saveJobDAO.isExisted(userId, recruitmentId);
    }

    @Transactional
    @Override
    public void addSaveJob(int userId, int recruitmentId) {
        User user = userDAO.getUserById(userId);
        Recruitment recruitment = recruitmentDAO.getRecruitmentById(recruitmentId);
        SaveJob saveJob = new SaveJob(recruitment, user);
        saveJobDAO.addSaveJob(saveJob);
    }

    @Transactional
    @Override
    public void deleteSaveJob(int userId, int recruitmentId) {
        saveJobDAO.deleteSaveJob(userId, recruitmentId);
    }

    @Override
    public Company getCompanyById(int id) {
        return companyDAO.getCompanyById(id);
    }

    @Override
    public boolean isFollowCompanyExisted(int userId, int companyId) {
        return followCompanyDAO.isExisted(userId, companyId);
    }

    @Transactional
    @Override
    public void addFollowCompany(int userId, int companyId) {
        User user = userDAO.getUserById(userId);
        Company company = companyDAO.getCompanyById(companyId);
        FollowCompany followCompany = new FollowCompany(user, company);
        followCompanyDAO.addFollowCompany(followCompany);
    }

    @Override
    public List<SaveJob> getSaveJobsByUserId(int userId, int pageNum) {
        return saveJobDAO.getSaveJobsByUserId(userId, pageNum);
    }

    @Override
    public int getNumOfSaveJobs(int userId) {
        return (int) saveJobDAO.getNumOfSaveJobs(userId);
    }

    @Transactional
    @Override
    public void deleteSaveJob(int id) {
        saveJobDAO.deleteSaveJob(id);
    }

    @Override
    public int getNumOfApplyPosts(int userId) {
        return (int) applyPostDAO.getApplyPosts(userId);
    }

    @Override
    public List<ApplyPost> getApplyPostsByUserId(int userId, int pageNum) {
        return applyPostDAO.getApplyPostByUserId(userId, pageNum);
    }

    @Transactional
    @Override
    public void deleteApplyPost(int id) {
        applyPostDAO.deleteApplyPost(id);
    }

    @Override
    public List<FollowCompany> getFollowCompaniesByUserId(int userId, int pageNum) {
        return followCompanyDAO.getFollowCompanies(userId, pageNum);
    }

    @Override
    public int getNumOfFollowCompanies(int userId) {
        return (int) followCompanyDAO.getFollowCompanies(userId);
    }

    @Transactional
    @Override
    public void deleteFollowCompany(int id) {
        followCompanyDAO.deleteFollowCompany(id);
    }

    @Override
    public int getNumOfRecruitments(int companyId) {
        return (int) recruitmentDAO.getNumOfRecruitmentsByCompanyId(companyId);
    }

    @Override
    public boolean isApplyPostExisted(int userId, int recruitmentId) {
        return applyPostDAO.isApplyPostExisted(userId, recruitmentId);
    }

    @Transactional
    @Override
    public void addCV(String cvName) {
        CV cv = new CV(cvName);
        cvDAO.addCV(cv);
    }

    @Transactional
    @Override
    public boolean updateCV(int userId, String newName) {
        User user = userDAO.getUserById(userId);

        if (user.getCv() == null) {
            CV cv = new CV(newName);
            cvDAO.addCV(cv);
            user.setCv(cv);
            return userDAO.updateUser(user);
        } else {
            return cvDAO.updateCV(user.getCv().getId(), newName);
        }
    }

    @Override
    public CV getCVById(int cvId) {
        return cvDAO.getCVById(cvId);
    }
}
