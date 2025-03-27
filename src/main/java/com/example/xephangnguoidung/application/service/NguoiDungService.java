package com.example.xephangnguoidung.application.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.xephangnguoidung.data.entity.NguoiDung;
import com.example.xephangnguoidung.data.entity.dto.RegisterDTO;
import com.example.xephangnguoidung.data.enums.CapBac;
import com.example.xephangnguoidung.data.enums.VaiTro;
import com.example.xephangnguoidung.data.repository.DiemNguoiDungRepository;
import com.example.xephangnguoidung.data.repository.NguoiDungRepository;

@Service
public class NguoiDungService {
    private final NguoiDungRepository nguoiDungRepository;
    private final DiemNguoiDungRepository diemNguoiDungRepository;

    public NguoiDungService(NguoiDungRepository nguoiDungRepository, DiemNguoiDungRepository diemNguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.diemNguoiDungRepository = diemNguoiDungRepository;
    }

    // ✅ 1️⃣ Tạo người dùng
    public NguoiDung luuNguoiDung(NguoiDung nguoiDung) {
        if (nguoiDungRepository.existsByTenDangNhap(nguoiDung.getTenDangNhap())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
        }
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại!");
        }
        return nguoiDungRepository.save(nguoiDung);
    }

    // ✅ 2️⃣ Lấy người dùng theo ID
    public NguoiDung layNguoiDungById(Long id) {
        return nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
    }

    // ✅ 3️⃣ Lấy người dùng theo tên đăng nhập
    public NguoiDung layNguoiDungBangTenDangNhap(String tenDangNhap) {
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap);
        if (nguoiDung == null) {
            throw new RuntimeException("Không tìm thấy người dùng có tên đăng nhập: " + tenDangNhap);
        }
        return nguoiDung;
    }

    // ✅ 4️⃣ Lấy tất cả người dùng
    public List<NguoiDung> layTatCaNguoiDung() {
        return nguoiDungRepository.findAll();
    }

    // ✅ 5️⃣ Sửa thông tin người dùng
    @Transactional
    public NguoiDung suaNguoiDung(Long id, NguoiDung nguoiDung) {
        NguoiDung nguoiDungHienTai = layNguoiDungById(id);

        if (!nguoiDung.getEmail().equals(nguoiDungHienTai.getEmail()) &&
                nguoiDungRepository.findByEmail(nguoiDung.getEmail()) != null) {
            throw new RuntimeException("Email đã tồn tại!");
        }
        nguoiDungHienTai.setTenDangNhap(nguoiDung.getTenDangNhap());
        nguoiDungHienTai.setMatKhau(nguoiDung.getMatKhau());
        nguoiDungHienTai.setEmail(nguoiDung.getEmail());
        nguoiDungHienTai.setVaiTro(nguoiDung.getVaiTro());
        nguoiDungHienTai.setSoLanDangNhap(nguoiDung.getSoLanDangNhap());

        // ✅ Cập nhật cấp bậc nếu cần
        capNhatCapBac(nguoiDungHienTai.getId());

        return nguoiDungRepository.save(nguoiDungHienTai);
    }

    // ✅ 6️⃣ Xóa người dùng
    @Transactional
    public void xoaNguoiDungBangId(Long id) {
        if (!nguoiDungRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy người dùng để xóa!");
        }
        nguoiDungRepository.deleteById(id);
    }

    // ✅ 8️⃣ Tìm kiếm người dùng
    public List<NguoiDung> timKiemNguoiDung(String keyword) {
        return nguoiDungRepository.findByTenDangNhapContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }

    // ✅ 9️⃣ Cập nhật cấp bậc
    @Transactional
    public void capNhatCapBac(Long nguoiDungId) {
        // 1. Lấy người dùng theo ID
        NguoiDung nguoiDung = layNguoiDungById(nguoiDungId);

        // 2. Tính tổng điểm
        Integer tongDiem = diemNguoiDungRepository.tinhTongDiemByNguoiDungId(nguoiDungId);
        if (tongDiem == null) {
            tongDiem = 0;
        }

        // 3. Xác định cấp bậc
        CapBac capBacMoi;
        if (tongDiem >= 2000) {
            capBacMoi = CapBac.VIP;
        } else if (tongDiem >= 1000) {
            capBacMoi = CapBac.KIM_CUONG;
        } else if (tongDiem >= 700) {
            capBacMoi = CapBac.BACH_KIM;
        } else if (tongDiem >= 300) {
            capBacMoi = CapBac.VANG;
        } else if (tongDiem >= 100) {
            capBacMoi = CapBac.BAC;
        } else {
            capBacMoi = CapBac.DONG;
        }

        // 4. Cập nhật cấp bậc nếu thay đổi
        if (!capBacMoi.equals(nguoiDung.getCapBac())) {
            nguoiDung.setCapBac(capBacMoi);
            nguoiDungRepository.save(nguoiDung);
        }
    }

    // số lượng người dùng
    public long soLuongNguoiDung() {
        return this.nguoiDungRepository.count();
    }

    // xử lý đăng nhập lấy bằng email
    public NguoiDung getNguoiDungByEmail(String email) {
        return this.nguoiDungRepository.findByEmail(email);
    }

    // xử lý đăng ký
    public NguoiDung registerDTOtoNguoiDung(RegisterDTO registerDTO) {
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setTenDangNhap(registerDTO.getTenDangNhap());
        nguoiDung.setEmail(registerDTO.getEmail());
        nguoiDung.setMatKhau(registerDTO.getMatKhau());
        nguoiDung.setVaiTro(registerDTO.getVaiTro());
        return nguoiDung;
    }
    // lấy id người dùng hiện tại

    public Long layIdNguoiDungHienTai(String email) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email);
        if (nguoiDung == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        return nguoiDung.getId();
    }

     public boolean isAdminExists() {
        return nguoiDungRepository.existsByVaiTro(VaiTro.ADMIN);
    }
}