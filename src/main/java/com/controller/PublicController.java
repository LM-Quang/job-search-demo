package com.controller;

import com.entity.Category;
import com.entity.Company;
import com.entity.Recruitment;
import com.entity.User;
import com.helper.Helper;
import com.service.PublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class PublicController {
    private final PublicService service;

    @Autowired
    public PublicController(PublicService service) {
        this.service = service;
    }

    // Hiển thị View home page
    @GetMapping({"/", "/{userId}"})
    public String showHomePage(@PathVariable(required = false) String userId,
                               @RequestParam(required = false) String msg,
                               Model model) {
        long numberOfApplicants = service.getNumOfApplicants();
        long numOfCompanies = service.getNumOfCompanies();
        long numOfRecruitments = service.getNumOfRecruitments();

        List<Category> categories = service.getCategories();
        List<Recruitment> recruitments = service.getTopRecruitments();
        Company company = service.getTopCompany();

        if (userId != null) {
            User user = service.getUserById(Integer.parseInt(userId));
            model.addAttribute("user", user);
        }

        if (msg != null) {
            model.addAttribute(msg, true);
        }

        model.addAttribute("numberOfApplicants", numberOfApplicants);
        model.addAttribute("numberOfCompanies", numOfCompanies);
        model.addAttribute("numberOfRecruitments", numOfRecruitments);
        model.addAttribute("categories", categories);
        model.addAttribute("recruitments", recruitments);
        model.addAttribute("company", company);
        return "public/home";
    }

    // Hiển thị View danh sách đơn tuyển dụng
    // Có phân trang
    // Giá trị msg hiển thị các thông báo:
    // - Lưu / bỏ lưu đơn tuyển dụng
    // - Ứng tuyển thành công / thất bại
    @GetMapping({"/recruitment", "/recruitment/{userId}"})
    public String showRecruitmentPage(@PathVariable(required = false) String userId,
                                      @RequestParam(required = false) String pageNum,
                                      @RequestParam(required = false) String msg,
                                      Model model) {
        // Nếu trong đường dẫn không có giá trị số trang thì mặc định là 1
        // ngược lại thì bằng giá trị trong đường dẫn
        int pageNumber;
        if (pageNum == null) {
            pageNumber = Helper.DEFAULT_PAGE;
        } else {
            pageNumber = Integer.parseInt(pageNum);
        }

        List<Recruitment> recruitments = service.getRecruitments(pageNumber);
        Company company = service.getTopCompany();
        int numOfRecruitment = (int) service.getNumOfRecruitments();
        int numOfPages = Helper.getNumOfPages(numOfRecruitment, Helper.RECRUITMENT_PER_PAGE);

        if (userId != null) {
            User user = service.getUserById(Integer.parseInt(userId));
            model.addAttribute("user", user);
        }

        // Nếu trong đường dẫn có giá trị msg thì cho vào model
        if (msg != null) {
            model.addAttribute(msg, true);
        }

        model.addAttribute("recruitments", recruitments);
        model.addAttribute("company", company);
        model.addAttribute("numOfPages", numOfPages);
        model.addAttribute("pageNum", pageNumber);
        return "public/recruitment";
    }

    // Hiển thị view Recruitment search
    // Tìm recruitment dựa vào keyword và searchType
    // searchType bao gồm:
    // - searchByName: tìm theo tên công việc
    // - searchByCompany: tìm theo tên công ty
    // - searchByLocation: tìm theo địa điểm
    @GetMapping({"/recruitment/search/{pageNum}", "/recruitment/search/{pageNum}/{userId}"})
    public String showResultSearchPage(@PathVariable String pageNum,
                                       @PathVariable(required = false) String userId,
                                       @RequestParam String keyword,
                                       @RequestParam String searchType,
                                       Model model) {

        List<Recruitment> recruitments = service.getRecruitments(searchType, keyword, Integer.parseInt(pageNum));
        int numOfRecruitmentsFound = (int) service.getNumOfRecruitmentsBySearch(searchType, keyword);
        int numOfPages = Helper.getNumOfPages(numOfRecruitmentsFound, Helper.RECRUITMENT_PER_PAGE);

        long numberOfApplicants = service.getNumOfApplicants();
        long numOfCompanies = service.getNumOfCompanies();
        long numOfRecruitments = service.getNumOfRecruitments();

        if (userId != null) {
            User user = service.getUserById(Integer.parseInt(userId));
            model.addAttribute("user", user);
        }

        model.addAttribute("numberOfApplicants", numberOfApplicants);
        model.addAttribute("numberOfCompanies", numOfCompanies);
        model.addAttribute("numberOfRecruitments", numOfRecruitments);
        model.addAttribute("recruitments", recruitments);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("numOfPages", numOfPages);
        model.addAttribute("pageNum", Integer.parseInt(pageNum));
        return "public/result-search";
    }

    // Hàm xử lý tìm recruitment
    // Nếu keyword rỗng thì lấy tất cả đơn
    // Ngược lại tìm theo keyword và searchType
    @PostMapping("/recruitment/search")
    public String processResultSearch(@RequestParam String keyword,
                                       @RequestParam String searchType,
                                      @RequestParam String userId) {
        if (keyword.trim().isEmpty()) {
            keyword = Helper.EMPTY;
        } else {
            keyword = Helper.removeAccent(keyword);
        }

        if (userId != null) {
            return "redirect:search/" + Helper.DEFAULT_PAGE + "/" + userId
                    + "?keyword=" + keyword + "&searchType=" + searchType;
        }
        return "redirect:search/" + Helper.DEFAULT_PAGE
                + "?keyword=" + keyword + "&searchType=" + searchType;
    }
}
