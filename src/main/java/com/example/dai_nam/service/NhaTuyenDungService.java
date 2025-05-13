package com.example.dai_nam.service;

import com.example.dai_nam.model.*;
import com.example.dai_nam.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class NhaTuyenDungService {
	

	
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
    
    public NhaTuyenDung xemThongTinCongTy(String email) {
        return nhaTuyenDungRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm nhà tuyển dung với email: " + email));
    }

    // 1. Cập nhật thông tin nhà tuyển dụng (chỉ sửa tài khoản của mình)
    @Transactional
    public NhaTuyenDung updateNhaTuyenDung(Integer idNguoiCapNhat, Integer idNhaTuyenDung,
                                           NhaTuyenDung updatedNhaTuyenDung) {
        Optional<NhaTuyenDung> optionalNhaTuyenDung = nhaTuyenDungRepository.findById(idNhaTuyenDung);
        if (optionalNhaTuyenDung.isEmpty()) {
            throw new IllegalArgumentException("Nhà tuyển dụng không tồn tại.");
        }

        NhaTuyenDung existing = optionalNhaTuyenDung.get();

        // Chỉ cho phép sửa thông tin cá nhân của chính mình
        if (!existing.getIdNhaTuyenDung().equals(idNguoiCapNhat)) {
            throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa thông tin này.");
        }

        // Cập nhật các trường được phép
        if (updatedNhaTuyenDung.getSoDienThoai() != null) {
            existing.setSoDienThoai(updatedNhaTuyenDung.getSoDienThoai());
        }
        if (updatedNhaTuyenDung.getDiaChi() != null) {
            existing.setDiaChi(updatedNhaTuyenDung.getDiaChi());
        }
        if (updatedNhaTuyenDung.getTrangWeb() != null) {
            existing.setTrangWeb(updatedNhaTuyenDung.getTrangWeb());
        }
        if (updatedNhaTuyenDung.getMoTaCongTy() != null) {
            existing.setMoTaCongTy(updatedNhaTuyenDung.getMoTaCongTy());
        }

        // Chỉ cập nhật mật khẩu nếu có
        String newPassword = updatedNhaTuyenDung.getMatKhau();
        if (newPassword != null && !newPassword.isEmpty()) {
            existing.setMatKhau(bCryptPasswordEncoder.encode(newPassword));
        }

        return nhaTuyenDungRepository.save(existing);
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
    @Transactional
    public BaiDangTuyenDung updateBaiDangTuyenDung(int idBaiDang, int idNguoiCapNhat, BaiDangTuyenDung updatedBaiDang) {
        BaiDangTuyenDung baiDang = baiDangTuyenDungRepository.findById(idBaiDang)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài đăng tuyển dụng!"));

        // 1) Kiểm tra quyền: admin hoặc chủ bài đăng
        boolean isAdmin = quanTriVienRepository.existsById(idNguoiCapNhat);
        boolean isOwner = baiDang.getNhaTuyenDung().getIdNhaTuyenDung().equals(idNguoiCapNhat);
        if (!isAdmin && !isOwner) {
            throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa bài đăng này!");
        }

        // 2) Nếu đã có ứng viên ứng tuyển thì không cho sửa
        boolean hasApplicants = donUngTuyenRepository.existsByBaiDangTuyenDung_IdBaiDang(idBaiDang);
        if (hasApplicants) {
            throw new IllegalStateException(
                "Bài đăng đã có ứng viên ứng tuyển, không thể chỉnh sửa."
            );
        }

        // 3) So sánh và cập nhật các trường
        boolean changed = false;
        if (!Objects.equals(baiDang.getTieuDe(), updatedBaiDang.getTieuDe())) {
            baiDang.setTieuDe(updatedBaiDang.getTieuDe());
            changed = true;
        }
        if (!Objects.equals(baiDang.getMoTa(), updatedBaiDang.getMoTa())) {
            baiDang.setMoTa(updatedBaiDang.getMoTa());
            changed = true;
        }
        if (!Objects.equals(baiDang.getYeuCau(), updatedBaiDang.getYeuCau())) {
            baiDang.setYeuCau(updatedBaiDang.getYeuCau());
            changed = true;
        }
        if (!Objects.equals(baiDang.getMucLuong(), updatedBaiDang.getMucLuong())) {
            baiDang.setMucLuong(updatedBaiDang.getMucLuong());
            changed = true;
        }
        if (!Objects.equals(baiDang.getDiaDiem(), updatedBaiDang.getDiaDiem())) {
            baiDang.setDiaDiem(updatedBaiDang.getDiaDiem());
            changed = true;
        }

        // Kiểm tra và cập nhật các trường bổ sung
        if (!Objects.equals(baiDang.getLoaiCongViec(), updatedBaiDang.getLoaiCongViec())) {
            baiDang.setLoaiCongViec(updatedBaiDang.getLoaiCongViec());
            changed = true;
        }
        if (baiDang.getSoLuongTuyen() != updatedBaiDang.getSoLuongTuyen()) {
            baiDang.setSoLuongTuyen(updatedBaiDang.getSoLuongTuyen());
            changed = true;
        }
        if (!Objects.equals(baiDang.getHanNop(), updatedBaiDang.getHanNop())) {
            baiDang.setHanNop(updatedBaiDang.getHanNop());
            changed = true;
        }
        if (updatedBaiDang.getBanner() != null && !updatedBaiDang.getBanner().isEmpty()) {
            baiDang.setBanner(updatedBaiDang.getBanner());
            changed = true;
        }
        // Nếu có thay đổi, chuyển trạng thái về "CHO_DUYET"
        if (changed) {
            baiDang.setTrangThai(BaiDangTuyenDung.TrangThaiBaiDang.CHO_DUYET);
        }

        return baiDangTuyenDungRepository.save(baiDang);
    }




    // 4. Xóa bài đăng tuyển dụng
    @Transactional
    public void deleteBaiDangTuyenDung(int idBaiDang, int idNguoiXoa) {
        BaiDangTuyenDung baiDang = baiDangTuyenDungRepository.findById(idBaiDang)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài đăng tuyển dụng!"));

        boolean isAdmin = quanTriVienRepository.existsById(idNguoiXoa);
        boolean isOwner = baiDang.getNhaTuyenDung().getIdNhaTuyenDung().equals(idNguoiXoa);
        if (!isAdmin && !isOwner) {
            throw new IllegalArgumentException("Bạn không có quyền xóa bài đăng này!");
        }
        
        // Kiểm tra điều kiện: chỉ xóa nếu CHỜ DUYỆT hoặc chưa có ứng viên
        boolean isChoDuyet = baiDang.getTrangThai() == BaiDangTuyenDung.TrangThaiBaiDang.CHO_DUYET;
        boolean hasNoApplicants = !donUngTuyenRepository.existsByBaiDangTuyenDung_IdBaiDang(idBaiDang);
        
        if (!isChoDuyet && !hasNoApplicants) {
            throw new IllegalStateException("Chỉ có thể xóa bài đăng chưa duyệt hoặc chưa có ứng viên ứng tuyển.");
        }

        // Nếu thỏa điều kiện thì xóa
        baiDangTuyenDungRepository.deleteById(idBaiDang);
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
        NhaTuyenDung ntv = baiDang.getNhaTuyenDung();
        if (!ntv.getIdNhaTuyenDung().equals(idNhaTuyenDung)) {
            throw new IllegalArgumentException("Bạn không có quyền duyệt ứng viên này!");
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
     // Hiện tại bạn đang viết
        ungTuyen.setTrangThai(chapNhan ? "Đã chấp nhận" : "Bị từ chối");
        ungTuyen.setNgayPhanHoi(now);

        // --- XÂY DỰNG EMAIL ---
        String subject = chapNhan
            ? "Chúc mừng! Bạn đã được chấp nhận vị trí " + baiDang.getTieuDe()
            : "Kết quả đơn ứng tuyển: Bạn không được chọn cho vị trí " + baiDang.getTieuDe();

        StringBuilder body = new StringBuilder();
        body.append("Xin chào ").append(ungTuyen.getSinhVien().getHoTen()).append(",\n\n");
        if (chapNhan) {
            body.append("Chúc mừng bạn đã được CHẤP NHẬN vào vị trí sau:\n");
        } else {
            body.append("Rất tiếc, bạn đã KHÔNG được chọn cho vị trí sau:\n");
        }
        // Thông tin vị trí
        body.append("Tiêu đề: ").append(baiDang.getTieuDe()).append("\n")
            .append("Mức lương: ").append(baiDang.getMucLuong()).append("\n")
            .append("Địa điểm: ").append(baiDang.getDiaDiem()).append("\n")
            .append("Loại công việc: ").append(baiDang.getLoaiCongViec()).append("\n\n");
        // Thông tin nhà tuyển dụng
        body.append("Thông tin nhà tuyển dụng:\n")
            .append("Tên công ty: ").append(ntv.getTenCongTy()).append("\n")
            .append("Email: ").append(ntv.getEmail()).append("\n")
            .append("SĐT: ").append(ntv.getSoDienThoai()).append("\n")
            .append("Địa chỉ: ").append(ntv.getDiaChi()).append("\n\n");
        // Lời kết
        if (chapNhan) {
            body.append("Hẹn gặp bạn trong buổi phỏng vấn hoặc ngày nhận việc sắp tới!\n");
        } else {
            body.append("Chúng tôi mong muốn được hợp tác với bạn trong các cơ hội tiếp theo.\n");
        }
        body.append("\nTrân trọng,\n").append(ntv.getTenCongTy());

        // --- GỌI GỬI EMAIL ---
        sendEmail(
          ungTuyen.getSinhVien().getEmail(),
          subject,
          body.toString()
        );

        // Tạo notification trên UI
        thongbaoService.createThongBao(new ThongBao(
            ungTuyen.getSinhVien().getIdSinhVien(),
            ThongBao.LoaiNguoiNhan.SINH_VIEN,
            chapNhan
                ? "Bạn đã được chấp nhận vị trí: " + baiDang.getTieuDe()
                : "Đơn ứng tuyển của bạn đã bị từ chối."
        ));

        return donUngTuyenRepository.save(ungTuyen);
    }

    @Transactional
    public void xoaDonUngTuyen(int idDon, int idNhaTuyenDung) {
        DonUngTuyen don = donUngTuyenRepository.findById(idDon)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn ứng tuyển với id = " + idDon));
        
        // Kiểm tra quyền: chỉ chủ bài đăng (nhà tuyển dụng) mới được xóa
        if (!don.getBaiDangTuyenDung().getNhaTuyenDung().getIdNhaTuyenDung().equals(idNhaTuyenDung)) {
            throw new AccessDeniedException("Bạn không có quyền xóa đơn này!");
        }
        
        donUngTuyenRepository.deleteById(idDon);
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
    
    public BaiDangTuyenDung getChiTietBaiDang(int idBaiDang, int idNguoiXem) {
        BaiDangTuyenDung baiDang = baiDangTuyenDungRepository.findById(idBaiDang)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài đăng với ID: " + idBaiDang));

        // Kiểm tra quyền: admin hoặc chủ bài đăng
        boolean isAdmin = quanTriVienRepository.existsById(idNguoiXem);
        boolean isOwner = baiDang.getNhaTuyenDung().getIdNhaTuyenDung().equals(idNguoiXem);
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Bạn không có quyền xem bài đăng này");
        }

        return baiDang;
    }
}
