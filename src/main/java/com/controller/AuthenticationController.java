package com.controller;

import com.entity.User;
import com.helper.Helper;
import com.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService service;

    @Autowired
    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    // Hiển thị View Login / Register
    @GetMapping({"/login", "/login/{attribute}"})
    public String showLoginPage(@PathVariable(required = false) String attribute, Model model) {
        // Nếu attribute có tồn tại thì cho vào model
        if (attribute != null) {
            // ============== Login ==================
            // Trường hợp Email không tồn tại trong DB => email không chính xác
            if (attribute.equals("emailLoginError")) {
                model.addAttribute("emailLoginError", Helper.EMAIL_NOT_CORRECT);
            }

            // Trường hợp mật khẩu sai
            if (attribute.equals("passwordLoginError")) {
                model.addAttribute("passwordLoginError", Helper.PASSWORD_NOT_CORRECT);
            }
            // ============== Login ==================

            // ============= Register ================
            // Trường hợp nhập lại mật khẩu sai
            if (attribute.equals("rePasswordError")) {
                model.addAttribute("rePasswordError", Helper.PASSWORD_NOT_CORRECT);
            }

            // Trường hợp email đã tồn tại
            if (attribute.equals("emailExistedError")) {
                model.addAttribute("emailExistedError", Helper.EMAIL_EXISTED);
            }

            // Trường hợp lưu tài khoản vào DB thất bại
            if (attribute.equals("msg_register_error")) {
                model.addAttribute("msg_register_error", true);
            }

            // Trường hợp đăng kí tài khoản thành công
            if (attribute.equals("msg_register_success")) {
                model.addAttribute("msg_register_success", true);
            }
            // ============= Register ================
        }
        return "public/login";
    }

    // Xử lý login
    // Nếu tài khoản hợp lệ thì chuyển hướng đến trang Home. Ngược lại hiển thị lỗi
    // Lỗi:
    // - sai mật khẩu hoặc email
    @PostMapping("/processLogin")
    public String processLogin(@RequestParam("email") String email,
                                @RequestParam("password") String password) {
        // Lấy thông tin User từ Database thông qua email
        User user = service.getUserByEmail(email);

        // Trường hợp Email không tồn tại trong DB => email không chính xác
        if (user == null) {
            return "redirect:login/" + "emailLoginError";
        }

        // Trường hợp mật khẩu sai
        if(!user.getPassword().equals(password)) {
            return "redirect:login/" + "passwordLoginError";
        }

        // Nếu không có lỗi thì chuyển đến trang Home kèm userId để xác định đã đăng nhập thành công
        return "redirect:/" + user.getId();
    }

    // Xử lý register
    // Nếu tài khoản mới hợp lệ thì thông báo thành công. Ngược lại hiển thị lỗi
    // Lỗi:
    // - nhập lại mật khẩu sai
    // - email đã tồn tại
    @PostMapping("/processRegister")
    public String processRegister(@RequestParam("email") String email,
                                  @RequestParam("fullName") String fullName,
                                  @RequestParam("password") String password,
                                  @RequestParam("rePassword") String rePassword,
                                  @RequestParam("roleId") String roleId) {
        // Trường hợp nhập lại mật khẩu sai
        if (!password.equals(rePassword)) {
            return "redirect:login/" + "rePasswordError";
        }

        // Trường hợp email đã tồn tại
        if (service.isEmailExisted(email)) {
            return "redirect:login/" + "emailExistedError";
        }

        // Trường hợp lưu tài khoản vào DB thất bại
        if (!service.addUser(email, fullName, password, Integer.parseInt(roleId))) {
            return "redirect:login/" + "msg_register_error";
        }

        // Đăng kí tài khoản thành công
        // Nếu tài khoản là Nhà tuyển dụng thì tạo Công ty đi kèm với user này
        // Thông báo đăng kí tài khoản thành công
        User user = service.getUserByEmail(email);
        if (user.getRole().getId() == Helper.ROLE_HR_ID) {
            service.addCompany(user);
        }
        return "redirect:login/" + "msg_register_success";
    }

    // Xử lý logout
    @GetMapping("/logout")
    public String logoutProcess() {
        // Chuyển hướng đến trang Home không kèm userId
        return "redirect:/";
    }
}
