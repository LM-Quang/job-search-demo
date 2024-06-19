package com.controller;

import com.entity.*;
import com.helper.Helper;
import com.service.StorageService;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequestMapping("/user")
@MultipartConfig
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private StorageService storageService;

    // ========================= Common ============================
    // Xử lý gửi email xác thực tài khoản
    @PostMapping("/confirm-account")
    public String confirmAccount(@RequestParam String userId) {
        int idUser = Integer.parseInt(userId);
        service.sendConfirmEmail(idUser);
        return "redirect:profile/" + idUser;
    }

    @GetMapping("/processConfirm/{userId}")
    public String processConfirm(@PathVariable String userId) {
        int idUser = Integer.parseInt(userId);
        String msg;
        if (service.confirmAccount(idUser)) {
            msg = "msg_confirm_success";
        } else {
            msg = "msg_confirm_error";
        }
        return "redirect:/user/profile/" + idUser + "/" + msg;
    }

    // View User Profile
    @GetMapping({"/profile/{userId}", "/profile/{userId}/{attribute}"})
    public String showProfilePage(@PathVariable String userId,
                                  @PathVariable(required = false) String attribute,
                                  @RequestParam(required = false) String email,
                                  Model model) {
        User user = null;
        Company company = null;

        // Lấy thông tin User, Company từ userId trong URL
        if (userId != null) {
            int id = Integer.parseInt(userId);
            user = service.getUserById(id);
            company = service.getCompanyByUserId(id);
        }

        // Xử lý modal attribute trong đường dẫn
        if (attribute != null) {
            // Trường hợp User email mới nhập đã tồn tại trong DB
            if (attribute.equals("existedEmail")) {
                model.addAttribute("existedEmail", email);
                model.addAttribute("emailExistedError", Helper.EMAIL_EXISTED);
            } else {
                model.addAttribute(attribute, true);
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("company", company);
        return "public/profile";
    }

    // Xử lý cập nhật thông tin User
    // Nếu cập nhật email mới thì kiểm tra tồn tại trong DB
    // Nếu email mới có tồn tại trong DB thì thông báo lỗi
    // Ngược lại thông báo cập nhật thành công
    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam("userId") String id,
                                @RequestParam("email") String email,
                                @RequestParam("fullName") String fullName,
                                @RequestParam("address") String address,
                                @RequestParam("phoneNumber") String phoneNumber,
                                @RequestParam("description") String description) {
        int userId = Integer.parseInt(id);

        User user = service.getUserById(userId);
        // Kiểm tra email có cập nhật hay không
        // email mới trùng với email hiện tại => không cập nhật
        if (!user.getEmail().equals(email)) {
            // Kiểm tra email có tồn tại trong DB
            // Nếu có thông báo lỗi
            boolean isExisted = service.isEmailExisted(userId, email);
            if (isExisted) {
                return "redirect:profile/" + user.getId() + "/" + "existedEmail" + "?email=" + email;
            }
        }

        // Cập nhật thông tin
        boolean isUpdated = service.updateUser(userId, email, fullName, address, phoneNumber, description);
        String msgAttr;
        // Thông báo cập nhật thành công hoặc thất bại
        if (isUpdated) {
            user = service.getUserById(userId);
            msgAttr = "msg_register_success";
        } else {
            msgAttr = "msg_register_error";
        }

        return "redirect:profile/" + user.getId() + "/" + msgAttr;
    }

    // Xử lý cập nhật thông tin Company
    @PostMapping("/update-company")
    public String updateCompany(@RequestParam("userId") String idUser,
                                @RequestParam("companyId") String idCompany,
                                @RequestParam("companyEmail") String companyEmail,
                                @RequestParam("companyName") String companyName,
                                @RequestParam("companyAddress") String companyAddress,
                                @RequestParam("companyPhoneNumber") String companyPhoneNumber,
                                @RequestParam("companyDescription") String companyDescription) {
        int userId = Integer.parseInt(idUser);
        int companyId = Integer.parseInt(idCompany);

        User user = service.getUserById(userId);

        // Cập nhật thông tin
        boolean isUpdated = service.updateCompany(companyId, companyEmail,
                companyName, companyAddress, companyPhoneNumber, companyDescription);
        String msgAttr;
        // Thông báo cập nhật thành công hoặc thất bại
        if (isUpdated) {
            msgAttr = "msg_register_success";
        } else {
            msgAttr = "msg_register_error";
        }

        return "redirect:profile/" + user.getId() + "/" + msgAttr;
    }

    // Hiển thị view chi tiết đơn tuyển dụng
    // Xử lý userId trong đường dẫn
    // - Nếu không có thì hiển thị danh sách các đơn tuyển dụng liên quan
    // - Nếu có userId thì lấy User dựa vào id và cho vào model
    // - Nếu User là HR thì hiển thị Các ứng viên đã ứng tuyển
    // - Nếu User là Applicant thì hiển thị các đơn liên quan
    @GetMapping({"/detail-post/{recruitmentId}", "/detail-post/{recruitmentId}/{userId}"})
    public String showDetailPostPage(@PathVariable(required = false) String userId,
                                     @PathVariable String recruitmentId,
                                     @RequestParam(required = false) String msg,
                                     Model model) {
        int idRecruitment = Integer.parseInt(recruitmentId);
        Recruitment recruitment = service.getRecruitmentById(idRecruitment);
        int idCompany = recruitment.getCompany().getId();
        int idCategory = recruitment.getCategory().getId();

        User user;
        if (userId != null) {
            user = service.getUserById(Integer.parseInt(userId));
            model.addAttribute("user", user);

            if (user.getRole().getId() == Helper.ROLE_HR_ID) {
                List<ApplyPost> applyPosts = service.getApplyPostsByRecruitmentId(idRecruitment);
                model.addAttribute("applyPosts", applyPosts);
            } else {
                List<Recruitment> relatedRecruitments = service.getRelatedRecruitments(idRecruitment, idCompany, idCategory);
                model.addAttribute("relatedRecruitments", relatedRecruitments);
            }
        } else {
            List<Recruitment> relatedRecruitments =
                    service.getRelatedRecruitments(idRecruitment, idCompany, idCategory);
            model.addAttribute("relatedRecruitments", relatedRecruitments);
        }

        if (msg != null) {
            model.addAttribute(msg, true);
        }

        model.addAttribute("recruitment", recruitment);
        return "public/detail-post";
    }

    // View chi tiết công ty
    // Giá trị msg thông báo theo dõi công ty thành công / thất bại
    @GetMapping({"/detail-company/{companyId}", "/detail-company/{companyId}/{userId}"})
    public String showDetailCompanyPage(@PathVariable String companyId,
                                        @PathVariable(required = false) String userId,
                                        @RequestParam(required = false) String msg,
                                        Model model) {
        if (userId != null) {
            User user = service.getUserById(Integer.parseInt(userId));
            model.addAttribute("user", user);
        }

        Company company = service.getCompanyById(Integer.parseInt(companyId));
        model.addAttribute("company", company);

        if (msg != null) {
            model.addAttribute(msg, true);
        }
        return "public/detail-company";
    }

    // Xử lý thao tác theo dõi công ty
    // Nếu chưa theo dõi thì lưu vào theo dỗi và gửi msg theo dõi thành công
    // Hoặc đã theo dõi thì gửi msg đã theo dõi
    @GetMapping("/follow-company/{companyId}/{userId}")
    public String followCompany(@PathVariable String companyId,
                                @PathVariable String userId) {
        int idUser = Integer.parseInt(userId);
        int idCompany = Integer.parseInt(companyId);

        boolean isExisted = service.isFollowCompanyExisted(idUser, idCompany);
        String msg;
        if (isExisted) {
            msg = "msg_follow_error";
        } else {
            service.addFollowCompany(idUser, idCompany);
            msg = "msg_follow_success";
        }
        return "redirect:/user/detail-company/" + companyId + "/" + userId + "?msg=" + msg;
    }
    // ============xxx========== Common =============xxx============

    // ========================= HR ==========================
    // View danh sách đơn tuyển của công ty
    // Có phân trang
    // Giá trị msg thông báo xoá đơn thành công / thất bại
    @GetMapping({"/post-list/{userId}", "/post-list/{userId}/{pageNum}"})
    public String showPostListPage(@PathVariable String userId,
                                   @PathVariable(required = false) String pageNum,
                                   @RequestParam(required = false) String msg,
                                   Model model) {
        int id = Integer.parseInt(userId);
        int page;
        if (pageNum == null) {
            page = Helper.DEFAULT_PAGE;
        } else {
            page = Integer.parseInt(pageNum);
        }

        User user = service.getUserById(id);
        Company company = service.getCompanyByUserId(id);
        List<Recruitment> recruitments = service.getRecruitmentsByCompanyId(company.getId(), page);
        int numOfPages = Helper.getNumOfPages(company.getRecruitments().size(), Helper.RECRUITMENT_PER_PAGE);

        model.addAttribute("user", user);
        model.addAttribute("recruitments", recruitments);
        model.addAttribute("numOfPages", numOfPages);
        model.addAttribute("pageNum", page);

        if (msg != null) {
            model.addAttribute(msg, true);
        }
        return "public/post-list";
    }

    // Xử lý xoá đơn tuyển dụng
    // Nếu không ai ứng tuyển thì xoá và thông báo xoá thành công / thất bại
    // Nếu có người ứng tuyển thông báo có người ứng tuyển
    @PostMapping("/processDeleteJob")
    public String processDeleteJob(@RequestParam("userId") String userId,
                                   @RequestParam("recruitmentId") String recruitmentId) {
        boolean isDeleted = service.deleteRecruitmentById(Integer.parseInt(recruitmentId));
        if (isDeleted) {
            return "redirect:post-list/" + userId + "?msg=" + "msg_register_success";
        }
        return "redirect:post-list/" + userId + "?msg=" + "msg_register_error";
    }

    // View đăng bài tuyển dụng
    // Giá trị msg thông báo đăng tuyển thành công / thất bại
    @GetMapping("/post-job/{userId}")
    public String showPostJobPage(@PathVariable String userId,
                                  @RequestParam(required = false) String msg,
                                  Model model) {
        int id = Integer.parseInt(userId);

        User user = service.getUserById(id);
        List<JobType> types = service.getJobTypes();
        List<Category> categories = service.getCategories();

        model.addAttribute("user", user);
        model.addAttribute("types", types);
        model.addAttribute("categories", categories);

        if (msg != null) {
            model.addAttribute(msg, true);
        }
        return "public/post-job";
    }

    // Xử lý đăng tuyển
    // Gửi msg thông báo đăng tuyển thành công / thất bại
    @PostMapping("/processAddJob")
    public String postJobProcess(@RequestParam("userID") String userID,
                                 @RequestParam("title") String title,
                                 @RequestParam("description") String description,
                                 @RequestParam("experience") String experience,
                                 @RequestParam("quantity") String quantity,
                                 @RequestParam("address") String address,
                                 @RequestParam("deadline") String deadline,
                                 @RequestParam("salary") String salary,
                                 @RequestParam("typeId") String typeId,
                                 @RequestParam("categoryId") String categoryId) {
        int num = Integer.parseInt(quantity);
        int idType = Integer.parseInt(typeId);
        int idCategory = Integer.parseInt(categoryId);
        int idUser = Integer.parseInt(userID);
        boolean isAdded = service.addRecruitment(title, description, experience, num,
                address, deadline, salary, idType, idCategory, idUser);
        if (isAdded) {
            return "redirect:post-job/" + userID + "?msg=" + "msg_register_success";
        }
        return "redirect:post-job/" + userID + "?msg=" + "msg_register_error";
    }

    // View cập nhật đơn tuyển dụng
    // Giá trị msg thông báo cập nhật thành công / thất bại
    @GetMapping("/edit-job/{userId}/{recruitmentId}")
    public String showEditJobPage(@PathVariable String userId,
                                  @PathVariable String recruitmentId,
                                  @RequestParam(required = false) String msg,
                                  Model model) {
        User user = service.getUserById(Integer.parseInt(userId));
        Recruitment recruitment = service.getRecruitmentById(Integer.parseInt(recruitmentId));
        List<JobType> types = service.getJobTypes();
        List<Category> categories = service.getCategories();

        model.addAttribute("user", user);
        model.addAttribute("recruitment", recruitment);
        model.addAttribute("types", types);
        model.addAttribute("categories", categories);

        if (msg != null) {
            model.addAttribute(msg, true);
        }
        return "public/edit-job";
    }

    // Xử lý cập nhật đơn tuyển dụng
    // Gửi msg thông báo cập nhật thành công / thất bại
    @PostMapping("/processEditJob")
    public String processEditJob(@RequestParam("userId") String userId,
                                 @RequestParam("recruitmentId") String recruitmentId,
                                 @RequestParam("title") String title,
                                 @RequestParam("description") String description,
                                 @RequestParam("experience") String experience,
                                 @RequestParam("quantity") String quantity,
                                 @RequestParam("address") String address,
                                 @RequestParam("deadline") String deadline,
                                 @RequestParam("salary") String salary,
                                 @RequestParam("typeId") String typeId,
                                 @RequestParam("categoryId") String categoryId) {
        boolean isUpdated =
                service.updateRecruitment(Integer.parseInt(recruitmentId), title, description, experience,
                        Integer.parseInt(quantity), address, deadline, salary,
                        Integer.parseInt(typeId), Integer.parseInt(categoryId));
        if (isUpdated) {
            return "redirect:edit-job/" + userId + "/" + recruitmentId + "?msg=" + "msg_register_success";
        }
        return "redirect:edit-job/" + userId + "/" + recruitmentId + "?msg=" + "msg_register_error";
    }

    // View danh sách ứng viên ứng tuyển
    // Có phân trang
    @GetMapping({"/list-user/{userId}", "/list-user/{userId}/{pageNum}"})
    public String showListUserPage(@PathVariable String userId,
                                   @PathVariable(required = false) String pageNum,
                                   Model model) {
        int idUser = Integer.parseInt(userId);
        int page;
        if (pageNum == null) {
            page = Helper.DEFAULT_PAGE;
        } else {
            page = Integer.parseInt(pageNum);
        }

        User user = service.getUserById(idUser);
        Company company = service.getCompanyByUserId(idUser);
        List<ApplyPost> applyPostsPerPage = service.getApplyPostsFromRecruitment(company.getRecruitments(), page);

        int numOfApplyPosts = service.getApplyPosts(company.getRecruitments());
        int numOfPages = Helper.getNumOfPages(numOfApplyPosts, Helper.APPLY_POST_PER_PAGE);

        model.addAttribute("user", user);
        model.addAttribute("applyPosts", applyPostsPerPage);
        model.addAttribute("numOfPages", numOfPages);
        model.addAttribute("pageNum", page);
        return "public/list-user";
    }
    // =============xxx========= HR ===========xxx============

    // ========================= Applicant =========================
    // Xử lý cập nhật Cv của ứng viên
    // Nếu chưa có cv thì cập nhật trường cv của user, lưu tên cv vào bảng cv và lưu file cv mới
    // Nếu có cv thì cập nhật lại trường cv của user, cập nhật lại tên cv trong bảng cv, xoá file cv cũ và lưu file mới
    @PostMapping("/update-cv")
    public String updateCV(@RequestParam String userId,
                           @RequestParam MultipartFile file) {
        int idUser = Integer.parseInt(userId);
        User user = service.getUserById(idUser);

        if (user.getCv() == null) {
            storageService.store(file);
        } else {
            storageService.deleteFile(user.getCv().getFileName());
            storageService.store(file);
        }

        if (service.updateCV(idUser, file.getOriginalFilename())) {
            return "redirect:profile/" + user.getId() + "/" + "msg_register_success";
        }
        return "redirect:profile/" + user.getId() + "/" + "msg_register_error";
    }

    // Xử lý xoá cv
    // Gửi thông báo xoá thành công / thất bại
    @PostMapping("/delete-cv")
    public String updateCV(@RequestParam String userId,
                           @RequestParam String cvId) {
        int idUser = Integer.parseInt(userId);
        int idCV = Integer.parseInt(cvId);

        boolean isDeleted = service.deleteUserCV(idUser);
        String msgAttr;
        if (isDeleted) {
            service.deleteCV(idCV);
            msgAttr = "msg_register_success";
        } else {
            msgAttr = "msg_register_error";
        }
        return "redirect:profile/" + idUser + "/" + msgAttr;
    }

    // Xử lý xem cv
    @GetMapping("/open-cv/{cvName}")
    public ResponseEntity<InputStreamResource> openCV(@PathVariable String cvName) throws FileNotFoundException {
        Path path = storageService.load(cvName);
        File file = path.toFile();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + cvName);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);
    }

    // Xử lý ứng tuyển
    // Nếu đã ứng tuyển rồi thì thông báo đã ứng tuyển
    // Xử lý nếu ứng tuyển với cv mới thì lưu tên cv vào bảng cv và lưu file cv mới
    @PostMapping("/apply-job")
    public String processApplyJob(@RequestParam String userId,
                                  @RequestParam String recruitmentId,
                                  @RequestParam String page,
                                  @RequestParam String applyMethod,
                                  @RequestParam String textType1,
                                  @RequestParam String textType2,
                                  @RequestParam MultipartFile file) {
        int idUser = Integer.parseInt(userId);
        int idRecruitment = Integer.parseInt(recruitmentId);
        User user = service.getUserById(idUser);

        if (service.isApplyPostExisted(idUser, idRecruitment)) {
            if (page.equals("home")) {
                return "redirect:/" + user.getId() + "?msg=" + "msg_already_applied";
            }
        }

        String msg;
        if (applyMethod.equals(Helper.APPLY_WITH_EXISTED_CV)) {
            if (user.getCv() == null) {
                msg = "msg_cv_error";
            } else {
                if (service.addApplyPost(user.getCv().getFileName(), textType1, idUser, idRecruitment)) {
                    msg = "msg_success";
                } else {
                    msg = "msg_error";
                }
            }
        } else {
            if (service.addApplyPost(file.getOriginalFilename(), textType2, idUser, idRecruitment)) {
                service.addCV(file.getOriginalFilename());
                storageService.store(file);
                msg = "msg_success";
            } else {
                msg = "msg_error";
            }
        }

        if (page.equals("home")) {
            return "redirect:/" + user.getId() + "?msg=" + msg;
        } else if (page.equals("recruitment")) {
            return "redirect:/recruitment/" + user.getId() + "?msg=" + msg;
        } else {
            return "redirect:detail-post/" + idRecruitment + "/" + idUser + "?msg=" + msg;
        }
    }

    // Xử lý theo dõi đơn tuyển dụng
    @GetMapping("/save-job/{userId}/{recruitmentId}")
    public String saveJob(@PathVariable String userId,
                          @PathVariable String recruitmentId,
                          @RequestParam String page) {
        int idUser = Integer.parseInt(userId);
        int idRecruitment = Integer.parseInt(recruitmentId);

        boolean isExisted = service.isSaveJobExisted(idUser, idRecruitment);
        String msg;
        if (isExisted) {
            service.deleteSaveJob(idUser, idRecruitment);
            msg = "msg_unsave_success";
        } else {
            service.addSaveJob(idUser, idRecruitment);
            msg = "msg_save_success";
        }

        if (page.equals(Helper.PAGE_HOME)) {
            return "redirect:/" + idUser + "?msg=" + msg;
        } else if (page.equals(Helper.PAGE_RECRUITMENT)) {
            return "redirect:/recruitment/" + idUser + "?msg=" + msg;
        } else {
            return "redirect:/user/detail-post/" + idRecruitment + "/" + idUser + "?msg=" + msg;
        }
    }

    @GetMapping({"/list-save-job/{userId}", "/list-save-job/{userId}/{pageNum}"})
    public String showListSaveJobPage(@PathVariable String userId,
                                      @PathVariable(required = false) String pageNum,
                                      @RequestParam(required = false) String msg,
                                      Model model) {
        int idUser = Integer.parseInt(userId);
        int page;
        if (pageNum == null) {
            page = Helper.DEFAULT_PAGE;
        } else {
            page = Integer.parseInt(pageNum);
        }

        User user = service.getUserById(idUser);
        List<SaveJob> saveJobs = service.getSaveJobsByUserId(idUser, page);

        int numOfSaveJobs = service.getNumOfSaveJobs(idUser);
        int numOfPages = Helper.getNumOfPages(numOfSaveJobs, Helper.SAVE_JOB_PER_PAGE);

        if (msg != null) {
            if (msg.equals("delete_success")) {
                model.addAttribute("delete_success", true);
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("saveJobs", saveJobs);
        model.addAttribute("numOfPages", numOfPages);
        model.addAttribute("pageNum", page);
        return "public/list-save-job";
    }

    @GetMapping("/delete-save-job/{userId}/{saveJobId}")
    public String deleteSaveJob(@PathVariable String userId,
                                @PathVariable String saveJobId) {
        int idUser = Integer.parseInt(userId);
        int idSaveJob = Integer.parseInt(saveJobId);
        service.deleteSaveJob(idSaveJob);
        return "redirect:/user/list-save-job/" + idUser + "?msg=" + "delete_success";
    }

    @GetMapping({"/list-apply-job/{userId}", "/list-apply-job/{userId}/{pageNum}"})
    public String showListApplyJobPage(@PathVariable String userId,
                                       @PathVariable(required = false) String pageNum,
                                       @RequestParam(required = false) String msg,
                                       Model model) {
        int idUser = Integer.parseInt(userId);
        int page;
        if (pageNum == null) {
            page = Helper.DEFAULT_PAGE;
        } else {
            page = Integer.parseInt(pageNum);
        }

        User user = service.getUserById(idUser);
        List<ApplyPost> applyPosts = service.getApplyPostsByUserId(idUser, page);
        int numOfApplyPosts = service.getNumOfApplyPosts(idUser);
        int numOfPages = Helper.getNumOfPages(numOfApplyPosts, Helper.APPLY_POST_PER_PAGE);

        if (msg != null) {
            if (msg.equals("delete_success")) {
                model.addAttribute("delete_success", true);
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("applyPosts", applyPosts);
        model.addAttribute("numOfPages", numOfPages);
        model.addAttribute("pageNum", page);
        return "public/list-apply-job";
    }

    @GetMapping("/delete-apply-job/{userId}/{applyPostId}")
    public String deleteApplyJob(@PathVariable String userId,
                                 @PathVariable String applyPostId) {
        int idUser = Integer.parseInt(userId);
        int idApplyPost = Integer.parseInt(applyPostId);
        service.deleteApplyPost(idApplyPost);
        return "redirect:/user/list-apply-job/" + idUser + "?msg=" + "delete_success";
    }

    @GetMapping({"/list-follow-company/{userId}", "/list-follow-company/{userId}/{pageNum}"})
    public String showListFollowCompanyPage(@PathVariable String userId,
                                            @PathVariable(required = false) String pageNum,
                                            @RequestParam(required = false) String msg,
                                            Model model) {
        int idUser = Integer.parseInt(userId);
        int page;
        if (pageNum == null) {
            page = Helper.DEFAULT_PAGE;
        } else {
            page = Integer.parseInt(pageNum);
        }

        User user = service.getUserById(idUser);
        List<FollowCompany> followCompanies = service.getFollowCompaniesByUserId(idUser, page);
        int numOfFollowCompanies = service.getNumOfFollowCompanies(idUser);
        int numOfPages = Helper.getNumOfPages(numOfFollowCompanies, Helper.FOLLOW_COMPANY_PER_PAGE);

        if (msg != null) {
            if (msg.equals("delete_success")) {
                model.addAttribute("delete_success", true);
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("followCompanies", followCompanies);
        model.addAttribute("numOfPages", numOfPages);
        model.addAttribute("pageNum", page);
        return "public/list-follow-company";
    }

    @GetMapping("/delete-follow-company/{userId}/{followCompanyId}")
    public String deleteFollowCompany(@PathVariable String userId,
                                 @PathVariable String followCompanyId) {
        int idUser = Integer.parseInt(userId);
        int idFollowCompany = Integer.parseInt(followCompanyId);
        service.deleteFollowCompany(idFollowCompany);
        return "redirect:/user/list-follow-company/" + idUser + "?msg=" + "delete_success";
    }

    @GetMapping("/post-company/{companyId}/{userId}")
    public String showPostCompanyPage(@PathVariable String companyId,
                                      @PathVariable(required = false) String userId,
                                      @RequestParam(required = false) String pageNum,
                                      Model model) {
        int idCompany = Integer.parseInt(companyId);

        int page;
        if (pageNum == null) {
            page = Helper.DEFAULT_PAGE;
        } else {
            page = Integer.parseInt(pageNum);
        }

        int numOfRecruitments = service.getNumOfRecruitments(idCompany);
        int numOfPages = Helper.getNumOfPages(numOfRecruitments, Helper.RECRUITMENT_PER_PAGE);

        Company company = service.getCompanyById(idCompany);
        List<Recruitment> recruitments = service.getRecruitmentsByCompanyId(idCompany, page);


        if (userId != null) {
            User user = service.getUserById(Integer.parseInt(userId));
            model.addAttribute("user", user);
        }

        model.addAttribute("company", company);
        model.addAttribute("recruitments", recruitments);
        model.addAttribute("numOfPages", numOfPages);
        model.addAttribute("pageNum", page);
        return "public/post-company";
    }
    // ============xxx========== Applicant ===========xxx===========
}
