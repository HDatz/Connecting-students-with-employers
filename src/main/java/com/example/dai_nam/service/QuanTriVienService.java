package com.example.dai_nam.service;

import com.example.dai_nam.model.*;
import com.example.dai_nam.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuanTriVienService {

    @Autowired
    private NhaTuyenDungRepository nhaTuyenDungRepository;

    @Autowired
    private SinhVienRepository sinhVienRepository;

    @Autowired
    private BaiVietHuongNghiepRepository baiVietHuongNghiepRepository;

    @Autowired
    private BaiDangTuyenDungRepository baiDangTuyenDungRepository;

    @Autowired
    private BinhLuanRepository binhLuanRepository;

    @Autowired
    private ThongBaoService thongBaoService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private QuanTriVienRepository quanTriVienRepository;
    
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //Quản lý tài khoản: Tạo tài khoản nhà tuyển dụng
    @Transactional
    public NhaTuyenDung createNhaTuyenDung(NhaTuyenDung nhaTuyenDung) {
    	String encodedPassword = bCryptPasswordEncoder.encode(nhaTuyenDung.getMatKhau());
    	nhaTuyenDung.setMatKhau(encodedPassword);
    	return nhaTuyenDungRepository.save(nhaTuyenDung);
    }
    
    public NhaTuyenDung getNhaTuyenDungById(Integer id) {
        return nhaTuyenDungRepository.findById(id).orElse(null);
    }
    
    public Optional<QuanTriVien> findByEmail(String email) {
        return quanTriVienRepository.findByEmail(email);
    }
    
    // Xem danh sách nhà tuyển dụng
    public List<NhaTuyenDung> getAllNhaTuyenDung() {
        return nhaTuyenDungRepository.findAll();
    }
    
    //Cập nhật nhà tuyển dụng 
    @Transactional
    public NhaTuyenDung updateNhaTuyenDung(NhaTuyenDung nhaTuyenDung) {
        // Lấy thông tin nhà tuyển dụng hiện tại
        NhaTuyenDung existingNhaTuyenDung = nhaTuyenDungRepository.findById(nhaTuyenDung.getIdNhaTuyenDung())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà tuyển dụng"));

        // Kiểm tra nếu email bị trùng trong Nhà Tuyển Dụng
        if (nhaTuyenDungRepository.existsByEmail(nhaTuyenDung.getEmail()) && 
            !nhaTuyenDung.getEmail().equals(existingNhaTuyenDung.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong danh sách nhà tuyển dụng.");
        }

        // Kiểm tra nếu email bị trùng trong Sinh Viên
        if (sinhVienRepository.existsByEmail(nhaTuyenDung.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong danh sách sinh viên.");
        }

        // Kiểm tra nếu email bị trùng trong Quản Trị Viên
        if (quanTriVienRepository.existsByEmail(nhaTuyenDung.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong danh sách quản trị viên.");
        }

        // Cập nhật thông tin
        existingNhaTuyenDung.setTenCongTy(nhaTuyenDung.getTenCongTy());
        existingNhaTuyenDung.setEmail(nhaTuyenDung.getEmail());
        existingNhaTuyenDung.setSoDienThoai(nhaTuyenDung.getSoDienThoai());
        existingNhaTuyenDung.setDiaChi(nhaTuyenDung.getDiaChi());
        existingNhaTuyenDung.setLinhVuc(nhaTuyenDung.getLinhVuc());
        existingNhaTuyenDung.setTrangWeb(nhaTuyenDung.getTrangWeb());
        existingNhaTuyenDung.setMoTaCongTy(nhaTuyenDung.getMoTaCongTy());

        // Nếu có thay đổi mật khẩu, mã hóa lại
        if (nhaTuyenDung.getMatKhau() != null && !nhaTuyenDung.getMatKhau().isEmpty()) {
            existingNhaTuyenDung.setMatKhau(bCryptPasswordEncoder.encode(nhaTuyenDung.getMatKhau()));
        }

        return nhaTuyenDungRepository.save(existingNhaTuyenDung);
    }



    // # Quản lý tài khoản: Tạo tài khoản sinh viên
    
    // Xem danh sách sinh viên
    public List<SinhVien> getAllSinhVien() {
        return sinhVienRepository.findAll();
    }
    
    @Transactional
    public SinhVien createSinhVien(SinhVien sinhVien) {
    	String encodedPassword = bCryptPasswordEncoder.encode(sinhVien.getMatKhau());
        sinhVien.setMatKhau(encodedPassword); // Set mật khẩu đã mã hóa
        return sinhVienRepository.save(sinhVien);
    }
    
    public SinhVien getSinhVienById(Integer id) {
        return sinhVienRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public SinhVien updateSinhVien(SinhVien sinhVien) {
        // Lấy thông tin sinh viên hiện tại
        SinhVien existingSinhVien = sinhVienRepository.findById(sinhVien.getIdSinhVien())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên"));

        // Kiểm tra nếu email bị trùng trong SinhVien
        if (sinhVienRepository.existsByEmail(sinhVien.getEmail()) && 
            !sinhVien.getEmail().equals(existingSinhVien.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong danh sách sinh viên.");
        }

        // Kiểm tra nếu email bị trùng trong Nhà Tuyển Dụng
        if (nhaTuyenDungRepository.existsByEmail(sinhVien.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong danh sách nhà tuyển dụng.");
        }

        // Kiểm tra nếu email bị trùng trong Quản Trị Viên
        if (quanTriVienRepository.existsByEmail(sinhVien.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong danh sách quản trị viên.");
        }

        // Cập nhật thông tin
        existingSinhVien.setNganhHoc(sinhVien.getNganhHoc());
        existingSinhVien.setHoTen(sinhVien.getHoTen());
        existingSinhVien.setEmail(sinhVien.getEmail());
        existingSinhVien.setNamTotNghiep(sinhVien.getNamTotNghiep());
        existingSinhVien.setSoDienThoai(sinhVien.getSoDienThoai());
        existingSinhVien.setDiaChi(sinhVien.getDiaChi());

        // Nếu có thay đổi mật khẩu, mã hóa lại
        if (sinhVien.getMatKhau() != null && !sinhVien.getMatKhau().isEmpty()) {
            existingSinhVien.setMatKhau(bCryptPasswordEncoder.encode(sinhVien.getMatKhau()));
        }

        return sinhVienRepository.save(existingSinhVien);
    }


    
    // # Quản lý tài khoản: Xóa tài khoản nhà tuyển dụng hoặc sinh viên theo email
    @Transactional
    public void deleteUser(String email) {
        nhaTuyenDungRepository.deleteByEmail(email);
        sinhVienRepository.deleteByEmail(email);
    }
    
    
    // Lấy lại mật khẩu
    @Transactional
    public void resetPassword(String email, String newPassword) {
        Optional<NhaTuyenDung> nhaTuyenDung = nhaTuyenDungRepository.findByEmail(email);
        Optional<SinhVien> sinhVien = sinhVienRepository.findByEmail(email);

        // Mã hóa mật khẩu trước khi lưu vào database
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(newPassword);

        if (nhaTuyenDung.isPresent()) {
            nhaTuyenDung.get().setMatKhau(encryptedPassword); // Lưu mật khẩu mã hóa vào DB
            nhaTuyenDungRepository.save(nhaTuyenDung.get());
        } else if (sinhVien.isPresent()) {
            sinhVien.get().setMatKhau(encryptedPassword); // Lưu mật khẩu mã hóa vào DB
            sinhVienRepository.save(sinhVien.get());
        } else {
            throw new IllegalArgumentException("Người dùng không tồn tại");
        }

        // Gửi email với mật khẩu gốc (không mã hóa)
        sendEmail(email, "Mật khẩu của bạn đã được đặt lại",
                "Mật khẩu mới của bạn là: " + newPassword);
    }


    // # Phương thức gửi email
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    
    
// --------------------------- 
    // # Quản lý bài viết hướng nghiệp: Thêm bài viết mới
    @Transactional
    public BaiVietHuongNghiep addBaiViet(BaiVietHuongNghiep baiViet) {
        return baiVietHuongNghiepRepository.save(baiViet);
    }
    
    
    //cập nhật bài viết hướng nghiệp 
    @Transactional
    public BaiVietHuongNghiep updateBaiViet(Integer id, BaiVietHuongNghiep updatedBaiViet) {
        Optional<BaiVietHuongNghiep> optionalBaiViet = baiVietHuongNghiepRepository.findById(id);
        if (optionalBaiViet.isPresent()) {
            BaiVietHuongNghiep baiViet = optionalBaiViet.get();
            baiViet.setTieuDe(updatedBaiViet.getTieuDe());
            baiViet.setNoiDung(updatedBaiViet.getNoiDung());
            return baiVietHuongNghiepRepository.save(baiViet);
        } else {
            throw new IllegalArgumentException("Bài viết không tồn tại");
        }
    }


    // # Quản lý bài viết hướng nghiệp: Xóa bài viết hướng nghiệp
    @Transactional
    public void deleteBaiViet(Integer id) {
        baiVietHuongNghiepRepository.deleteById(id);
    }

    // # Quản lý bài tuyển dụng: Duyệt bài đăng tuyển dụng
    @Transactional
    public void approveBaiDangTuyenDung(Integer id) {
        Optional<BaiDangTuyenDung> baiDang = baiDangTuyenDungRepository.findById(id);
        if (baiDang.isPresent()) {
            baiDang.get().setTrangThai(BaiDangTuyenDung.TrangThaiBaiDang.DA_DUYET);
            baiDangTuyenDungRepository.save(baiDang.get());

            // # Gửi thông báo cho nhà tuyển dụng khi bài đăng được duyệt
            thongBaoService.createThongBao(new ThongBao(
                    baiDang.get().getNhaTuyenDung().getIdNhaTuyenDung(),
                    ThongBao.LoaiNguoiNhan.NHA_TUYEN_DUNG,
                    "Bài đăng của bạn đã được duyệt."));
        }
    }


    
    // # Quản lý bài viết hướng nghiệp: Cập nhật bài viết
    public BaiDangTuyenDung updateBaiDangTuyenDung(int idBaiDang, BaiDangTuyenDung updatedBaiDangTuyenDung, int idNhaTuyenDung) {
        Optional<BaiDangTuyenDung> optionalBaiDang = baiDangTuyenDungRepository.findById(idBaiDang);

        if (optionalBaiDang.isPresent()) {
            BaiDangTuyenDung existingBaiDang = optionalBaiDang.get();

            //Kiểm tra quyền sửa đổi (chỉ nhà tuyển dụng sở hữu mới được sửa)
            if (existingBaiDang.getNhaTuyenDung().getIdNhaTuyenDung() != idNhaTuyenDung) {
                throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa bài đăng này!");
            }

            //Cập nhật thông tin bài đăng
            existingBaiDang.setTieuDe(updatedBaiDangTuyenDung.getTieuDe());
            existingBaiDang.setMoTa(updatedBaiDangTuyenDung.getMoTa());
            existingBaiDang.setYeuCau(updatedBaiDangTuyenDung.getYeuCau());
            existingBaiDang.setMucLuong(updatedBaiDangTuyenDung.getMucLuong());
            existingBaiDang.setDiaDiem(updatedBaiDangTuyenDung.getDiaDiem());

            // ✅ Khi nhà tuyển dụng sửa bài đăng, chuyển về trạng thái "CHỜ DUYỆT"
            existingBaiDang.setTrangThai(BaiDangTuyenDung.TrangThaiBaiDang.CHO_DUYET);

            return baiDangTuyenDungRepository.save(existingBaiDang);
        }
        throw new IllegalArgumentException("Không tìm thấy bài đăng!");
    }
    
    // # Quản lý bài tuyển dụng: Từ chối bài đăng tuyển dụng
    @Transactional
    public void rejectBaiDangTuyenDung(Integer id) {
        Optional<BaiDangTuyenDung> baiDang = baiDangTuyenDungRepository.findById(id);
        if (baiDang.isPresent()) {
            baiDang.get().setTrangThai(BaiDangTuyenDung.TrangThaiBaiDang.TU_CHOI);
            baiDangTuyenDungRepository.save(baiDang.get());

            // # Gửi thông báo cho nhà tuyển dụng khi bài đăng bị từ chối
            thongBaoService.createThongBao(new ThongBao(
                    baiDang.get().getNhaTuyenDung().getIdNhaTuyenDung(),
                    ThongBao.LoaiNguoiNhan.NHA_TUYEN_DUNG,
                    "Bài đăng của bạn đã bị từ chối."));
        }
    }
    
    
    //Xóa Bài đăng tuyển dụng
    @Transactional
    public void deleteBaiDangTuyenDung(int idBaiDang, int idNguoiXoa) {
        Optional<BaiDangTuyenDung> optionalBaiDang = baiDangTuyenDungRepository.findById(idBaiDang);

        if (optionalBaiDang.isPresent()) {
            BaiDangTuyenDung baiDang = optionalBaiDang.get();

            boolean isAdmin = quanTriVienRepository.existsById(idNguoiXoa);
            boolean isOwner = baiDang.getNhaTuyenDung().getIdNhaTuyenDung() == idNguoiXoa;

            if (!isAdmin && !isOwner) {
                throw new IllegalArgumentException("Bạn không có quyền xóa bài đăng này!");
            }

            baiDangTuyenDungRepository.deleteById(idBaiDang);
        } else {
            throw new IllegalArgumentException("Không tìm thấy bài đăng tuyển dụng!");
        }
    }
   
    
    // # Quản lý bình luận: Xóa bình luận không phù hợp
    @Transactional
    public void deleteBinhLuan(Integer id) {
        binhLuanRepository.deleteById(id);
    }
    
    // Admin trả lời bình luận
    @Transactional
    public BinhLuan replyToComment(Integer binhLuanId, Integer adminId, String noiDung) {
        Optional<BinhLuan> optionalBinhLuan = binhLuanRepository.findById(binhLuanId);
        Optional<QuanTriVien> optionalAdmin = quanTriVienRepository.findById(adminId);

        if (optionalBinhLuan.isEmpty()) {
            throw new IllegalArgumentException("Bình luận không tồn tại.");
        }

        if (optionalAdmin.isEmpty()) {
            throw new IllegalArgumentException("Admin không tồn tại.");
        }

        BinhLuan binhLuanCha = optionalBinhLuan.get();

        BinhLuan phanHoi = new BinhLuan();
        phanHoi.setNoiDung(noiDung);
        phanHoi.setQuanTriVien(optionalAdmin.get());
        phanHoi.setBinhLuanCha(binhLuanCha);
        phanHoi.setBaiVietHuongNghiep(binhLuanCha.getBaiVietHuongNghiep());

        BinhLuan savedPhanHoi = binhLuanRepository.save(phanHoi);

        // Gửi thông báo cho người tạo bình luận gốc
        ThongBao thongBao = new ThongBao();
        thongBao.setIdNguoiNhan(binhLuanCha.getSinhVien() != null
                ? binhLuanCha.getSinhVien().getIdSinhVien()
                : binhLuanCha.getNhaTuyenDung().getIdNhaTuyenDung());
        thongBao.setNoiDung("Bình luận của bạn đã được admin phản hồi.");
        thongBao.setDaXem(false);
        thongBaoService.createThongBao(thongBao);

        // Gửi email thông báo
        String emailNguoiNhan = binhLuanCha.getSinhVien() != null
                ? binhLuanCha.getSinhVien().getEmail()
                : binhLuanCha.getNhaTuyenDung().getEmail();

        sendEmail(emailNguoiNhan, "Bình luận của bạn đã được phản hồi",
                "Admin đã phản hồi bình luận của bạn: \"" + noiDung + "\"");

        return savedPhanHoi;
    }

}
