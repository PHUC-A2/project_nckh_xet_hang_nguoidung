package com.example.xephangnguoidung.presentation.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.xephangnguoidung.application.service.BaiVietService;
import com.example.xephangnguoidung.application.service.BinhLuanService;
import com.example.xephangnguoidung.application.service.DiemNguoiDungService;
import com.example.xephangnguoidung.application.service.NguoiDungService;
import com.example.xephangnguoidung.data.entity.NguoiDung;

@Controller
public class AdminController {
    private final NguoiDungService nguoiDungService;
    private final BaiVietService baiVietService;
    private final BinhLuanService binhLuanService;
    private final DiemNguoiDungService diemNguoiDungService;

    public AdminController(NguoiDungService nguoiDungService, BaiVietService baiVietService,
            BinhLuanService binhLuanService, DiemNguoiDungService diemNguoiDungService) {
        this.nguoiDungService = nguoiDungService;
        this.baiVietService = baiVietService;
        this.binhLuanService = binhLuanService;
        this.diemNguoiDungService = diemNguoiDungService;
    }

    @GetMapping("/admin")
    public String trangChuAdmin(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        NguoiDung nguoiDung = this.nguoiDungService.getNguoiDungByEmail(username);
        model.addAttribute("nguoiDung", nguoiDung);
        // lấy số lượng người dùng
        Long slNguoiDung = this.nguoiDungService.soLuongNguoiDung();
        model.addAttribute("soLuongNguoiDung", slNguoiDung);

        // lấy số lượng bài viết
        Long slBaiViet = this.baiVietService.soLuongBaiViet();
        model.addAttribute("soLuongBaiViet", slBaiViet);

        // lấy số lượng bình luận
        Long slBinhLuan = this.binhLuanService.soLuongBinhLuan();
        model.addAttribute("soLuongBinhLuan", slBinhLuan);

        // lấy tổng số điểm
        Integer tongSoDiem = this.diemNguoiDungService.tongSoDiemTatCaNguoiDung();
        model.addAttribute("tongSoDiem", tongSoDiem);

        return "admin/trang_chu_admin";
    }
}