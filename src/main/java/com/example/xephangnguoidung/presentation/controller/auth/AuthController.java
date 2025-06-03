package com.example.xephangnguoidung.presentation.controller.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.xephangnguoidung.application.service.NguoiDungService;
import com.example.xephangnguoidung.data.entity.NguoiDung;
import com.example.xephangnguoidung.data.entity.dto.RegisterDTO;
import com.example.xephangnguoidung.data.enums.VaiTro;

@Controller
public class AuthController {

    private final NguoiDungService nguoiDungService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(NguoiDungService nguoiDungService, PasswordEncoder passwordEncoder) {
        this.nguoiDungService = nguoiDungService;
        this.passwordEncoder = passwordEncoder;
    }

    // register(đăng ký)
    @GetMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("registerNguoiDung", new RegisterDTO());
        model.addAttribute("danhSachVaiTro", VaiTro.values());
        return "auth/register"; // trả về trang đăng ký
    }

    @PostMapping("/register")
    public String getRegister(@ModelAttribute("registerNguoiDung") RegisterDTO registerDTO, Model model) {
        if (!registerDTO.getMatKhau().equals(registerDTO.getXacNhanMatKhau())) {
            model.addAttribute("error", "Mật khẩu và xác nhận mật khẩu không khớp.");
            model.addAttribute("registerNguoiDung", registerDTO);
            model.addAttribute("danhSachVaiTro", VaiTro.values());
            return "auth/register"; // Trả về trang đăng ký với thông báo lỗi
        }

        if (registerDTO.getVaiTro() == VaiTro.ADMIN && nguoiDungService.isAdminExists()) {
            model.addAttribute("error", "Chỉ có thể đăng ký một tài khoản admin. Vui lòng đăng ký với vai trò USER.");
            model.addAttribute("registerNguoiDung", registerDTO);
            model.addAttribute("danhSachVaiTro", VaiTro.values());
            return "auth/register"; // Trả về trang đăng ký với thông báo lỗi
        }

        NguoiDung nguoiDung = this.nguoiDungService.registerDTOtoNguoiDung(registerDTO); // xử dụng DTO

        // hash code
        String hashPassword = this.passwordEncoder.encode(nguoiDung.getMatKhau());
        nguoiDung.setMatKhau(hashPassword);

        nguoiDung.setVaiTro(nguoiDung.getVaiTro());
        this.nguoiDungService.luuNguoiDung(nguoiDung);

        System.out.println("Thong tin nguoi dung la:");
        System.out.println(nguoiDung.toString());
        System.out.println("Mat khau la: " + hashPassword);
        return "redirect:/login"; // trả về trang đăng nhập nếu đăng ký thành công
    }

    // Đăng nhập
    @GetMapping("/login")
    public String getLogin() {
        // model.addAttribute("loginError", false);
        return "auth/login"; // Trả về trang đăng nhập
    }

    // Không cần phương thức postLogin vì Spring Security sẽ xử lý đăng nhập
}