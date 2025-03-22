package com.example.xephangnguoidung.presentation.controller.user;

import com.example.xephangnguoidung.application.service.BaiVietService;
import com.example.xephangnguoidung.application.service.DiemNguoiDungService;
import com.example.xephangnguoidung.application.service.NguoiDungService;
import com.example.xephangnguoidung.data.entity.BaiViet;
import com.example.xephangnguoidung.data.entity.NguoiDung;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class HoSoController {
    private final NguoiDungService nguoiDungService;
    private final DiemNguoiDungService diemNguoiDungService;
    private final BaiVietService baiVietService;

    public HoSoController(NguoiDungService nguoiDungService, DiemNguoiDungService diemNguoiDungService,
            BaiVietService baiVietService) {
        this.nguoiDungService = nguoiDungService;
        this.diemNguoiDungService = diemNguoiDungService;
        this.baiVietService = baiVietService;
    }

    @GetMapping("/hoso")
    public String getHoSo(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        NguoiDung nguoiDung = this.nguoiDungService.getNguoiDungByEmail(username);
        List<BaiViet> danhSachBaiViet = this.baiVietService.layTatCaBaiViet();
        model.addAttribute("danhSachBaiViet", danhSachBaiViet);
        if (nguoiDung != null) {
            Long nguoiDungId = nguoiDung.getId();
            System.out.println("ID Nguoi Dung: " + nguoiDungId); // Kiểm tra ID trên console

            model.addAttribute("nguoiDung", nguoiDung);
            model.addAttribute("nguoiDungId", nguoiDungId); // Thêm ID vào model
            // model.addAttribute("user", nguoiDung);
            int tongDiem = diemNguoiDungService.tinhTongDiemByNguoiDungId(nguoiDung.getId());
            model.addAttribute("tongDiem", tongDiem);
            return "user/hoso_nguoidung";
        } else {
            return "error";
        }

    }

    @GetMapping("/thongtincanhan")
    public String userProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login"; // Chuyển hướng nếu chưa đăng nhập
        }

        NguoiDung nguoiDung = this.nguoiDungService.getNguoiDungByEmail(userDetails.getUsername());

        if (nguoiDung == null) {
            return "error"; // Trả về trang lỗi nếu không tìm thấy người dùng
        }

        // In log kiểm tra
        System.out.println("NguoiDung: " + nguoiDung);

        model.addAttribute("user", nguoiDung);
        return "user/thongtin_canhan";
    }

    // Sửa thông tin người dùng
    @PostMapping("/sua/{id}")
    public String suaNguoiDungById(@PathVariable Long id, @ModelAttribute NguoiDung request,
            RedirectAttributes redirectAttributes) {
        NguoiDung nguoiDung = nguoiDungService.layNguoiDungById(id);
        nguoiDung.setTenDangNhap(request.getTenDangNhap());
        // nguoiDung.setMatKhau(request.getMatKhau()); // không cho đổi mật khẩu vì không băm
        nguoiDung.setVaiTro(request.getVaiTro());
        if (request.getEmail() != null) {
            nguoiDung.setEmail(request.getEmail()); // không cho đổi email vì dùng email đăng nhập
        }

        // Lưu người dùng trước, sau đó cập nhật cấp bậc
        nguoiDungService.suaNguoiDung(id, nguoiDung);
        nguoiDungService.capNhatCapBac(nguoiDung.getId());

        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công!");

        return "redirect:/user/hoso";
    }

}