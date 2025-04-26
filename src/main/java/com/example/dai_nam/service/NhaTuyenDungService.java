package com.example.dai_nam.service;

import com.example.dai_nam.model.*;
import com.example.dai_nam.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class NhaTuyenDungService {
	
	@Autowired
	private SinhVienRepository sinhVienRepository;
	
    @Autowired
    private NhaTuyenDungRepository nhaTuyenDungRepository;

    @Autowired
    private BaiDangTuyenDungRepository baiDangTuyenDungRepository;

    @Autowired
    private DonUngTuyenRepository donUngTuyenRepository;

    @Autowired
    private ThongBaoService thongbaoService;

    @Autowired
    private QuanTriVienRepository quanTriVienRepository;
    
    @Autowired
    private BaiVietHuongNghiepRepository baiVietHuongNghiepRepository;
    
    @Autowired
    private ThongBaoService thongBaoService;

    @Autowired
    private JavaMailSender mailSender; // Gửi email thông báo
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // Lấy danh sách ứng viên của một bài đăng tuyển dụng
    public List<DonUngTuyen> getUngVienByBaiDang(int baiDangId) {
        return donUngTuyenRepository.findByBaiDangTuyenDung_IdBaiDang(baiDangId);
    }
    
    public Optional<NhaTuyenDung> findByEmail(String email) {
        return nhaTuyenDungRepository.findByEmail(email);
    }

    // 1. Cập nhật thông tin nhà tuyển dụng (chỉ sửa tài khoản của mình)
    @Transactional
    public NhaTuyenDung updateNhaTuyenDung(Integer idNguoiCapNhat, Integer idNhaTuyenDung,
                                           NhaTuyenDung updatedNhaTuyenDung) {
        Optional<NhaTuyenDung> optionalNhaTuyenDung = nhaTuyenDungRepository.findById(idNhaTuyenDung);
        if (optionalNhaTuyenDung.isEmpty()) {
            throw new IllegalArgumentException("Nhà tuyển dụng không tồn tại.");
        }

        NhaTuyenDung existingNhaTuyenDung = optionalNhaTuyenDung.get();

        // Kiểm tra quyền
        boolean isAdmin = quanTriVienRepository.existsById(idNguoiCapNhat);
        boolean isOwner = existingNhaTuyenDung.getIdNhaTuyenDung().equals(idNguoiCapNhat);

        if (!isAdmin && !isOwner) {
            throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa thông tin này.");
        }

        // Cập nhật mật khẩu nếu có
        String newPassword = updatedNhaTuyenDung.getMatKhau();
        if (newPassword != null && !newPassword.isEmpty()) {
            existingNhaTuyenDung.setMatKhau(bCryptPasswordEncoder.encode(newPassword));
        }

        // Kiểm tra và cập nhật email nếu thay đổi
        String newEmail = updatedNhaTuyenDung.getEmail();
        if (newEmail != null && !newEmail.equals(existingNhaTuyenDung.getEmail())) {

            if (nhaTuyenDungRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email đã tồn tại trong danh sách nhà tuyển dụng.");
            }

            if (sinhVienRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email đã tồn tại trong danh sách sinh viên.");
            }

            if (quanTriVienRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email đã tồn tại trong danh sách quản trị viên.");
            }

            existingNhaTuyenDung.setEmail(newEmail);
        }

        // Cập nhật các trường còn lại
        existingNhaTuyenDung.setTenCongTy(updatedNhaTuyenDung.getTenCongTy());
        existingNhaTuyenDung.setMoTaCongTy(updatedNhaTuyenDung.getMoTaCongTy());
        existingNhaTuyenDung.setDiaChi(updatedNhaTuyenDung.getDiaChi());
        existingNhaTuyenDung.setTrangWeb(updatedNhaTuyenDung.getTrangWeb());

        return nhaTuyenDungRepository.save(existingNhaTuyenDung);
    }


    // 2. Nhà tuyển dụng tạo bài đăng tuyển dụng (Chờ duyệt)
    public BaiDangTuyenDung createBaiTuyenDung(BaiDangTuyenDung baiTuyenDung, int idNhaTuyenDung) {
        Optional<NhaTuyenDung> nhaTuyenDungOpt = nhaTuyenDungRepository.findById(idNhaTuyenDung);
        if (nhaTuyenDungOpt.isEmpty()) {
            throw new IllegalArgumentException("Nhà tuyển dụng không tồn tại!");
        }
       
        baiTuyenDung.setNhaTuyenDung(nhaTuyenDungOpt.get()); // ✅ Gán thông tin đầy đủ
        baiTuyenDung.setNgayDang(new Timestamp(System.currentTimeMillis())); // ✅ Thêm ngày đăng
        baiTuyenDung.setTrangThai(BaiDangTuyenDung.TrangThaiBaiDang.CHO_DUYET); // ✅ Mặc định trạng thái

        return baiDangTuyenDungRepository.save(baiTuyenDung);
    }

    public BaiDangTuyenDung getBaiDangById(int id) {
        return baiDangTuyenDungRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài đăng với ID: " + id));
    }

    
    // 3. Nhà tuyển dụng cập nhật bài đăng tuyển dụng
    public BaiDangTuyenDung updateBaiDangTuyenDung(int idBaiDang, int idNguoiCapNhat, BaiDangTuyenDung updatedBaiDang) {
        Optional<BaiDangTuyenDung> optionalBaiDang = baiDangTuyenDungRepository.findById(idBaiDang);
        if (optionalBaiDang.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy bài đăng tuyển dụng!");
        }

        BaiDangTuyenDung baiDang = optionalBaiDang.get();
        boolean isAdmin = quanTriVienRepository.existsById(idNguoiCapNhat);
        boolean isOwner = baiDang.getNhaTuyenDung().getIdNhaTuyenDung().equals(idNguoiCapNhat);

        if (!isAdmin && !isOwner) {
            throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa bài đăng này!");
        }

        baiDang.setTieuDe(updatedBaiDang.getTieuDe());
        baiDang.setMoTa(updatedBaiDang.getMoTa());
        baiDang.setYeuCau(updatedBaiDang.getYeuCau());
        baiDang.setMucLuong(updatedBaiDang.getMucLuong());
        baiDang.setDiaDiem(updatedBaiDang.getDiaDiem());

        return baiDangTuyenDungRepository.save(baiDang);
    }


    // 4. Xóa bài đăng tuyển dụng
    @Transactional
    public void deleteBaiDangTuyenDung(int idBaiDang, int idNguoiXoa) {
        Optional<BaiDangTuyenDung> optionalBaiDang = baiDangTuyenDungRepository.findById(idBaiDang);

        if (optionalBaiDang.isPresent()) {
            BaiDangTuyenDung baiDang = optionalBaiDang.get();

            boolean isAdmin = quanTriVienRepository.existsById(idNguoiXoa);
            boolean isOwner = baiDang.getNhaTuyenDung().getIdNhaTuyenDung().equals(idNguoiXoa);

            if (!isAdmin && !isOwner) {
                throw new IllegalArgumentException("Bạn không có quyền xóa bài đăng này!");
            }

            baiDangTuyenDungRepository.deleteById(idBaiDang);
        } else {
            throw new IllegalArgumentException("Không tìm thấy bài đăng tuyển dụng!");
        }
    }


    // 5. Nhà tuyển dụng xem danh sách bài đăng của mình
    public List<BaiDangTuyenDung> getAllBaiDangTuyenDungByNhaTuyenDung(int nhaTuyenDungId, BaiDangTuyenDung.TrangThaiBaiDang trangThai) {
        if (trangThai != null) {
            return baiDangTuyenDungRepository.findByNhaTuyenDung_IdNhaTuyenDungAndTrangThai(nhaTuyenDungId, trangThai);
        } else {
            return baiDangTuyenDungRepository.findByNhaTuyenDung_IdNhaTuyenDung(nhaTuyenDungId);
        }
    }
    // 6. Nhà tuyển dụng duyệt ứng viên (Chấp nhận / Từ chối)
 
    @Transactional
    public DonUngTuyen xuLyUngVien(int ungTuyenId, int idNhaTuyenDung, boolean chapNhan) {
        DonUngTuyen ungTuyen = donUngTuyenRepository.findById(ungTuyenId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn ứng tuyển!"));

        BaiDangTuyenDung baiDang = ungTuyen.getBaiDangTuyenDung();
        if (baiDang.getNhaTuyenDung().getIdNhaTuyenDung() != idNhaTuyenDung) {
            throw new IllegalArgumentException("Bạn không có quyền duyệt ứng viên này!");
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        ungTuyen.setTrangThai(chapNhan ? "Đã chấp nhận" : "Đã từ chối");
        ungTuyen.setNgayPhanHoi(now);

        // gửi email
        sendEmail(
          ungTuyen.getSinhVien().getEmail(),
          chapNhan ? "Chúc mừng! Bạn đã được chấp nhận." : "Rất tiếc! Bạn bị từ chối.",
          chapNhan
            ? "Bạn đã được nhận vị trí: " + baiDang.getTieuDe()
            : "Nhà tuyển dụng đã từ chối đơn ứng tuyển của bạn."
        );

        // tạo notification trên UI
        thongBaoService.createThongBao(new ThongBao(
            ungTuyen.getSinhVien().getIdSinhVien(),
            ThongBao.LoaiNguoiNhan.SINH_VIEN,
            chapNhan
                ? "Bạn đã được chấp nhận vị trí: " + baiDang.getTieuDe()
                : "Đơn ứng tuyển của bạn đã bị từ chối."
        ));

        return donUngTuyenRepository.save(ungTuyen);
    }



    // 7. Thông báo đến nhà tuyển dụng khi có ứng viên ứng tuyển
    public void notifyNhaTuyenDung(int nhaTuyenDungId, String message) {
        ThongBao thongbao = new ThongBao();
        thongbao.setIdNguoiNhan(nhaTuyenDungId);
        thongbao.setLoaiNguoiNhan(ThongBao.LoaiNguoiNhan.NHA_TUYEN_DUNG);
		LocalDateTime now = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		thongbao.setNgayGui(now);
        thongbao.setNoiDung(message);
        thongbaoService.createThongBao(thongbao);
    }

    // ✅ 8. Gửi email thông báo
    private void sendEmail(String email, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
    
    public NhaTuyenDung getById(Integer id) {
        return nhaTuyenDungRepository.findById(id).orElse(null);
    }

    public BaiVietHuongNghiep getBaiVietById(Integer id) {
        return baiVietHuongNghiepRepository.findById(id).orElse(null);
    }
}
