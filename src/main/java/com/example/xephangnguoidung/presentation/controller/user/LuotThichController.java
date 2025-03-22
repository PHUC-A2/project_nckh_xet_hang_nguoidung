package com.example.xephangnguoidung.presentation.controller.user;

import com.example.xephangnguoidung.application.service.LuotThichService;
import com.example.xephangnguoidung.application.service.NguoiDungService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/user/luotthich")
public class LuotThichController {

    private final LuotThichService luotThichService;
    private final NguoiDungService nguoiDungService;

    public LuotThichController(LuotThichService luotThichService, NguoiDungService nguoiDungService) {
        this.luotThichService = luotThichService;
        this.nguoiDungService = nguoiDungService;
    }

    // @GetMapping("/{baiVietId}")
    // public RedirectView toggleLike(@PathVariable Long baiVietId,
    // RedirectAttributes redirectAttributes) {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // if (authentication == null || !authentication.isAuthenticated()) {
    // redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để thực hiện
    // thao tác này.");
    // return new RedirectView("/login");
    // }

    // Long nguoiDungId =
    // nguoiDungService.layIdNguoiDungHienTai(authentication.getName());
    // boolean daLike = luotThichService.kiemTraDaThich(nguoiDungId, baiVietId);

    // if (daLike) {
    // luotThichService.xoaLuotThich(nguoiDungId, baiVietId);
    // } else {
    // luotThichService.themLuotThich(nguoiDungId, baiVietId);
    // }

    // // Lấy lại số lượt thích sau khi cập nhật
    // int soLuotThich = luotThichService.demSoLuotThich(baiVietId);
    // redirectAttributes.addFlashAttribute("soLuotThich", soLuotThich);
    // return new RedirectView("/user/baiviet/chitiet/{id}"); // load lại trang
    // }
    @GetMapping("/{baiVietId}")
    public RedirectView toggleLike(@PathVariable Long baiVietId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để thực hiện thao tác này.");
            return new RedirectView("/login");
        }

        Long nguoiDungId = nguoiDungService.layIdNguoiDungHienTai(authentication.getName());
        boolean daLike = luotThichService.kiemTraDaThich(nguoiDungId, baiVietId);

        if (daLike) {
            luotThichService.xoaLuotThich(nguoiDungId, baiVietId);
        } else {
            luotThichService.themLuotThich(nguoiDungId, baiVietId);
        }

        // Lấy lại số lượt thích sau khi cập nhật
        int soLuotThich = luotThichService.demSoLuotThich(baiVietId);
        redirectAttributes.addFlashAttribute("soLuotThich", soLuotThich);
        return new RedirectView("/user/baiviet/chitiet/" + baiVietId); // load lại trang
    }
}