package com.example.xephangnguoidung.presentation.controller.user;

import com.example.xephangnguoidung.application.service.BinhLuanService;
import com.example.xephangnguoidung.application.service.NguoiDungService;
import com.example.xephangnguoidung.application.service.BaiVietService;
import com.example.xephangnguoidung.data.entity.BinhLuan;
import com.example.xephangnguoidung.data.entity.NguoiDung;
import com.example.xephangnguoidung.data.entity.BaiViet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user/binhluan")
public class BinhLuanController {

    private final BinhLuanService binhLuanService;
    private final NguoiDungService nguoiDungService;
    private final BaiVietService baiVietService;

    public BinhLuanController(BinhLuanService binhLuanService, NguoiDungService nguoiDungService,
            BaiVietService baiVietService) {
        this.binhLuanService = binhLuanService;
        this.nguoiDungService = nguoiDungService;
        this.baiVietService = baiVietService;
    }

    // Thêm bình luận
    @PostMapping("/tao")
    public String themBinhLuan(@RequestParam("noiDung") String noiDung,
            @RequestParam("baiVietId") Long baiVietId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String tenDangNhap = authentication.getName();
        NguoiDung nguoiDung = nguoiDungService.getNguoiDungByEmail(tenDangNhap); // tên đăng nhập mình dùng email

        BinhLuan binhLuan = new BinhLuan();
        binhLuan.setNoiDung(noiDung);
        binhLuan.setNguoiDung(nguoiDung);

        BaiViet baiViet = baiVietService.timBaiVietTheoId(baiVietId);
        binhLuan.setBaiViet(baiViet);

        binhLuanService.themBinhLuan(nguoiDung.getId(), binhLuan);
        Long binhLuanId = binhLuan.getId(); // Lấy ID của bình luận mới tạo

        // Chuyển hướng đến URL chứa ID của bình luận mới tạo
        // return "redirect:/user/baiviet/tatca#binhluan-" + binhLuanId;
        return "redirect:/user/baiviet/chitiet/" + baiVietId + "#binhluan-" + binhLuanId;
    }

    @GetMapping("/sua/{binhLuanId}")
    public String hienThiFormSuaBinhLuan(@PathVariable Long binhLuanId, Model model) {
        BinhLuan binhLuan = binhLuanService.layBinhLuanById(binhLuanId);
        model.addAttribute("binhLuan", binhLuan);
        return "user/sua_binhluan";
    }

    // Sửa bình luận
    @PostMapping("/sua/{binhLuanId}")
    public String suaBinhLuan(@PathVariable Long binhLuanId,
            @RequestParam("noiDung") String noiDung) {
        BinhLuan binhLuan = binhLuanService.layBinhLuanById(binhLuanId);
        binhLuan.setNoiDung(noiDung);
        binhLuanService.capNhatBinhLuan(binhLuan);
        return "redirect:/user/baiviet/tatca";
    }

    // Xóa bình luận
    @PostMapping("/xoa/{binhLuanId}")
    public String xoaBinhLuan(@PathVariable Long binhLuanId) {
        BinhLuan binhLuan = binhLuanService.layBinhLuanById(binhLuanId);
        Long baiVietId = binhLuan.getBaiViet().getId();
        binhLuanService.xoaBinhLuan(binhLuanId);
        return "redirect:/user/baiviet/chitiet/" + baiVietId;
    }
}