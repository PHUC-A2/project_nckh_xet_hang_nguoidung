package com.example.xephangnguoidung.data.enums;

public enum LoaiHoatDong {
    DANG_NHAP(10), // Đăng nhập
    VIET_BAI(200), // Viết bài
    BINH_LUAN(100), // Bình luận
    DUOC_THICH(100); // Được thích

    private final int diem;

    private LoaiHoatDong(int diem) {
        this.diem = diem;
    }

    public int getDiem() {
        return diem;
    }

}
