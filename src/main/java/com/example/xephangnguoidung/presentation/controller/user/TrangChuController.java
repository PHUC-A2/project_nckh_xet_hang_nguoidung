package com.example.xephangnguoidung.presentation.controller.user;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.xephangnguoidung.application.service.BaiVietService;
import com.example.xephangnguoidung.application.service.DiemNguoiDungService;
import com.example.xephangnguoidung.application.service.NguoiDungService;
import com.example.xephangnguoidung.data.entity.BaiViet;
import com.example.xephangnguoidung.data.entity.NguoiDung;
import com.example.xephangnguoidung.data.enums.VaiTro;

@Controller
public class TrangChuController {
    private final NguoiDungService nguoiDungService;
    private final DiemNguoiDungService diemNguoiDungService;
    private final BaiVietService baiVietService;

    public TrangChuController(NguoiDungService nguoiDungService, DiemNguoiDungService diemNguoiDungService,
            BaiVietService baiVietService) {
        this.nguoiDungService = nguoiDungService;
        this.diemNguoiDungService = diemNguoiDungService;
        this.baiVietService = baiVietService;
    }

    @GetMapping("/")
    public String bangXepHangNguoiDung(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<NguoiDung> danhSachNguoiDung;
        List<BaiViet> danhSachBaiViet;

        if (keyword != null && !keyword.isEmpty()) {
            danhSachBaiViet = baiVietService.timKiemBaiViet(keyword);
            danhSachNguoiDung = nguoiDungService.timKiemNguoiDung(keyword);
        } else {
            danhSachBaiViet = baiVietService.layTatCaBaiViet();
            danhSachNguoiDung = nguoiDungService.layTatCaNguoiDung();
        }

        if (danhSachNguoiDung.isEmpty()) {
            model.addAttribute("danhSachNguoiDung", Collections.emptyList());
        } else {
            // Lọc bỏ người dùng có vai trò là ADMIN
            List<NguoiDung> danhSachNguoiDungUser = danhSachNguoiDung.stream()
                    .filter(nguoiDung -> nguoiDung.getVaiTro() != VaiTro.ADMIN)
                    .collect(Collectors.toList());

            // Sắp xếp danh sách người dùng theo tổng điểm từ cao đến thấp
            Collections.sort(danhSachNguoiDungUser, (nd1, nd2) -> {
                int tongDiem1 = diemNguoiDungService.tinhTongDiemByNguoiDungId(nd1.getId());
                int tongDiem2 = diemNguoiDungService.tinhTongDiemByNguoiDungId(nd2.getId());
                return Integer.compare(tongDiem2, tongDiem1);
            });
            model.addAttribute("danhSachNguoiDung", danhSachNguoiDungUser);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        model.addAttribute("danhSachBaiViet", danhSachBaiViet);
        model.addAttribute("formatter", formatter);
        model.addAttribute("baiViet", new BaiViet());
        // Đảm bảo rằng diemNguoiDungService không bị null trong template
        model.addAttribute("diemNguoiDungService", diemNguoiDungService);
        return "user/trang_chu";
    }
}