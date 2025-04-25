//package com.example.dai_nam.service;
//
//import com.example.dai_nam.model.*;
//import com.example.dai_nam.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.sql.Timestamp;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class BaiDangTuyenDungService {
//
//    @Autowired
//    private BaiDangTuyenDungRepository baiDangTuyenDungRepository;
//
//    @Autowired
//    private DonUngTuyenRepository donUngTuyenRepository;
//
//    @Autowired
//    private ThongBaoService thongBaoService;
//
//    // Lấy danh sách tất cả bài đăng tuyển dụng
//    public List<BaiDangTuyenDung> getAllBaiDangTuyenDung() {
//        return baiDangTuyenDungRepository.findAll();
//    }
//
//    // Tìm kiếm bài đăng tuyển dụng theo tiêu đề
//    public List<BaiDangTuyenDung> searchBaiDangTuyenDung(String keyword) {
//        return baiDangTuyenDungRepository.findByTieuDeContaining(keyword);
//    }
//
//    // Thêm mới bài đăng tuyển dụng (Chỉ nhà tuyển dụng mới có quyền)
//    public Optional<BaiDangTuyenDung> addBaiDangTuyenDung(BaiDangTuyenDung baiDangTuyenDung, int nhaTuyenDungId) {
//        if (baiDangTuyenDung.getNhaTuyenDung().getIdNhaTuyenDung() != nhaTuyenDungId) {
//            throw new IllegalArgumentException("Bạn không có quyền tạo bài đăng này.");
//        }
//        baiDangTuyenDung.setNgayDang(new Timestamp(System.currentTimeMillis()));
//        baiDangTuyenDung.setTrangThai(BaiDangTuyenDung.TrangThaiBaiDang.CHO_DUYET);
//        return Optional.of(baiDangTuyenDungRepository.save(baiDangTuyenDung));
//    }
//
//    // Cập nhật bài đăng (Chỉ nhà tuyển dụng sở hữu bài đăng mới được cập nhật)
//    public Optional<BaiDangTuyenDung> updateBaiDangTuyenDung(BaiDangTuyenDung baiDangTuyenDung, int nhaTuyenDungId) {
//        if (baiDangTuyenDung.getNhaTuyenDung().getIdNhaTuyenDung() != nhaTuyenDungId) {
//            throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa bài đăng này.");
//        }
//        return Optional.of(baiDangTuyenDungRepository.save(baiDangTuyenDung));
//    }
//
//    // Xóa bài đăng (Chỉ xóa nếu không có đơn ứng tuyển)
//    public void deleteBaiDangTuyenDung(Integer id, int nhaTuyenDungId) {
//        Optional<BaiDangTuyenDung> baiDangTuyenDung = baiDangTuyenDungRepository.findById(id);
//        if (baiDangTuyenDung.isEmpty()) {
//            throw new IllegalArgumentException("Bài đăng không tồn tại.");
//        }
//        if (baiDangTuyenDung.get().getNhaTuyenDung().getIdNhaTuyenDung() != nhaTuyenDungId) {
//            throw new IllegalArgumentException("Bạn không có quyền xóa bài đăng này.");
//        }
//        if (!donUngTuyenRepository.findByBaiDangTuyenDung_IdBaiDang(id).isEmpty()) {
//            throw new IllegalArgumentException("Không thể xóa bài đăng vì có đơn ứng tuyển liên quan.");
//        }
//        baiDangTuyenDungRepository.deleteById(id);
//    }
//
//    // Duyệt bài đăng + gửi thông báo
//    public void approveBaiDangTuyenDung(Integer id) {
//        Optional<BaiDangTuyenDung> baiDang = baiDangTuyenDungRepository.findById(id);
//        if (baiDang.isPresent()) {
//            baiDang.get().setTrangThai(BaiDangTuyenDung.TrangThaiBaiDang.DA_DUYET);
//            baiDangTuyenDungRepository.save(baiDang.get());
//
//            // Gửi thông báo
//            thongBaoService.createThongBao(new ThongBao(
//                    baiDang.get().getNhaTuyenDung().getIdNhaTuyenDung(),
//                    ThongBao.LoaiNguoiNhan.NHA_TUYEN_DUNG,
//                    "Bài đăng của bạn đã được duyệt."));
//        }
//    }
//
//    // Từ chối bài đăng + gửi thông báo
//    public void rejectBaiDangTuyenDung(Integer id) {
//        Optional<BaiDangTuyenDung> baiDang = baiDangTuyenDungRepository.findById(id);
//        if (baiDang.isPresent()) {
//            baiDang.get().setTrangThai(BaiDangTuyenDung.TrangThaiBaiDang.TU_CHOI);
//            baiDangTuyenDungRepository.save(baiDang.get());
//
//            // Gửi thông báo
//            thongBaoService.createThongBao(new ThongBao(
//                    baiDang.get().getNhaTuyenDung().getIdNhaTuyenDung(),
//                    ThongBao.LoaiNguoiNhan.NHA_TUYEN_DUNG,
//                    "Bài đăng của bạn đã bị từ chối."));
//        }
//    }
//}
