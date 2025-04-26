package com.example.dai_nam.service;

import com.example.dai_nam.model.*;
import com.example.dai_nam.model.BaiDangTuyenDung.TrangThaiBaiDang;
import com.example.dai_nam.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SinhVienService {

    @Autowired
    private SinhVienRepository sinhVienRepository;

    @Autowired
    private BaiDangTuyenDungRepository baiDangTuyenDungRepository;

    @Autowired
    private BaiVietHuongNghiepRepository baiVietHuongNghiepRepository;

    @Autowired
    private BinhLuanRepository binhLuanRepository;

    @Autowired
    private QuanTriVienRepository quanTriVienRepository;

    @Autowired
    private ThongBaoService thongBaoService;
    
    @Autowired
    private NhaTuyenDungRepository nhaTuyenDungRepository;
    
    @Autowired
    private DonUngTuyenRepository donUngTuyenRepository;
    
    @Autowired
    private ThongBaoRepository thongBaoRepository;


    @Autowired
    private JavaMailSender mailSender;
    
    public List<NhaTuyenDung> getAllNhaTuyenDung() {
        return nhaTuyenDungRepository.findAll();
    }
    
	public NhaTuyenDung getNhaTuyenDungById(Integer id) {
		return nhaTuyenDungRepository.findById(id).orElse(null);
	}
    
	
    // 1. Lấy danh sách tất cả bài tuyển dụng
    public List<BaiDangTuyenDung> getAllBaiDangTuyenDung() {
        return baiDangTuyenDungRepository.findAll();
    }
    //Tìm Kiếm Sinh viên theo mail
    public Optional<SinhVien> findByEmail(String email) {
        return sinhVienRepository.findByEmail(email);
    }

    // 2. Tìm kiếm bài tuyển dụng theo tiêu đề hoặc địa điểm
    public List<BaiDangTuyenDung> searchBaiDangTuyenDung(String keyword) {
        return baiDangTuyenDungRepository.findByTieuDeContainingOrDiaDiemContaining(keyword, keyword);
    }

    // 3. Cập nhật thông tin sinh viên (chỉ chính chủ)
    @Transactional
    public SinhVien updateSinhVien(Integer idNguoiCapNhat, Integer idSinhVien, SinhVien updatedSinhVien) {
        Optional<SinhVien> optionalSinhVien = sinhVienRepository.findById(idSinhVien);
        if (optionalSinhVien.isPresent()) {
            SinhVien existingSinhVien = optionalSinhVien.get();

            // Kiểm tra nếu là admin hoặc chính sinh viên đó mới được quyền cập nhật
            boolean isAdmin = quanTriVienRepository.existsById(idNguoiCapNhat);
            boolean isOwner = existingSinhVien.getIdSinhVien().equals(idNguoiCapNhat);

            if (!isAdmin && !isOwner) {
                throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa thông tin này.");
            }
            
            if (!existingSinhVien.getEmail().equals(updatedSinhVien.getEmail())) {
                if (sinhVienRepository.existsByEmail(updatedSinhVien.getEmail())) {
                    throw new IllegalArgumentException("Email này đã được sử dụng.");
                }
                existingSinhVien.setEmail(updatedSinhVien.getEmail());
            }

            existingSinhVien.setHoTen(updatedSinhVien.getHoTen());
            existingSinhVien.setSoDienThoai(updatedSinhVien.getSoDienThoai());
            existingSinhVien.setDiaChi(updatedSinhVien.getDiaChi());
            existingSinhVien.setNganhHoc(updatedSinhVien.getNganhHoc());
            existingSinhVien.setNamTotNghiep(updatedSinhVien.getNamTotNghiep());
            existingSinhVien.setGioiThieu(updatedSinhVien.getGioiThieu());
            existingSinhVien.setDuongDanCv(updatedSinhVien.getDuongDanCv());
            existingSinhVien.setAvatar(updatedSinhVien.getAvatar());

            return sinhVienRepository.save(existingSinhVien);
        }
        throw new IllegalArgumentException("Sinh viên không tồn tại.");
    }

    // 4. Xem tất cả bài viết hướng nghiệp do admin viết
    public List<BaiVietHuongNghiep> getAllBaiVietHuongNghiep() {
        return baiVietHuongNghiepRepository.findAll();
    }

    // 5. Bình luận trong bài viết hướng nghiệp
    @Transactional
    public BinhLuan addBinhLuan(Integer sinhVienId, Integer baiVietId, String noiDung, Integer binhLuanChaId) {
        if (noiDung == null || noiDung.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung bình luận không được để trống.");
        }

        Optional<SinhVien> optionalSinhVien = sinhVienRepository.findById(sinhVienId);
        Optional<BaiVietHuongNghiep> optionalBaiViet = baiVietHuongNghiepRepository.findById(baiVietId);

        if (optionalSinhVien.isEmpty()) {
            throw new IllegalArgumentException("Sinh viên không tồn tại.");
        }
        if (optionalBaiViet.isEmpty()) {
            throw new IllegalArgumentException("Bài viết không tồn tại.");
        }

        BinhLuan binhLuan = new BinhLuan();
        binhLuan.setNoiDung(noiDung);
        binhLuan.setSinhVien(optionalSinhVien.get());
        binhLuan.setBaiVietHuongNghiep(optionalBaiViet.get());
        binhLuan.setNgayDang(LocalDateTime.now());

        // Nếu có bình luận cha, xử lý phản hồi
        if (binhLuanChaId != null) {
            Optional<BinhLuan> optionalBinhLuanCha = binhLuanRepository.findById(binhLuanChaId);
            if (optionalBinhLuanCha.isPresent()) {
                BinhLuan binhLuanCha = optionalBinhLuanCha.get();
                binhLuan.setBinhLuanCha(binhLuanCha);

                // Kiểm tra xem người nhận có email hay không
                if (binhLuanCha.getSinhVien() != null && binhLuanCha.getSinhVien().getEmail() != null) {
                    sendNotificationEmail(binhLuanCha.getSinhVien().getEmail(),
                            "Bình luận của bạn đã được phản hồi: " + noiDung);
                }

                // Gửi thông báo vào hệ thống
                ThongBao thongBao = new ThongBao();
                thongBao.setIdNguoiNhan(binhLuanCha.getSinhVien().getIdSinhVien());
                thongBao.setLoaiNguoiNhan(ThongBao.LoaiNguoiNhan.SINH_VIEN);
                thongBao.setNoiDung("Bình luận của bạn đã được phản hồi: " + noiDung);
                thongBao.setDaXem(false);
                thongBao.setNgayGui(LocalDateTime.now());

                thongBaoService.createThongBao(thongBao);
            }
        }

        return binhLuanRepository.save(binhLuan);
    }


    // 6. Gửi email thông báo khi có ai đó trả lời bình luận
    private void sendNotificationEmail(String email, String originalComment) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Bình luận của bạn đã được trả lời!");
        message.setText("Bình luận của bạn: \"" + originalComment + "\" đã có phản hồi. Hãy kiểm tra ngay!");
        mailSender.send(message);
    }

    @Transactional
    public void deleteBinhLuan(Integer idNguoiXoa, Integer idBinhLuan) {
        Optional<BinhLuan> optionalBinhLuan = binhLuanRepository.findById(idBinhLuan);

        if (optionalBinhLuan.isEmpty()) {
            throw new IllegalArgumentException("Bình luận không tồn tại.");
        }

        BinhLuan binhLuan = optionalBinhLuan.get();

        // Kiểm tra quyền: chỉ admin hoặc chủ nhân bình luận mới được xóa
        boolean isAdmin = quanTriVienRepository.existsById(idNguoiXoa);
        boolean isOwner = binhLuan.getSinhVien() != null && binhLuan.getSinhVien().getIdSinhVien().equals(idNguoiXoa);

        if (!isAdmin && !isOwner) {
            throw new IllegalArgumentException("Bạn không có quyền xóa bình luận này.");
        }

        // Nếu bình luận có phản hồi, cân nhắc xóa luôn hoặc giữ lại
        if (binhLuan.getPhanHois() != null && !binhLuan.getPhanHois().isEmpty()) {
            binhLuanRepository.deleteAll(binhLuan.getPhanHois()); // Xóa tất cả phản hồi
        }

        binhLuanRepository.delete(binhLuan); // Xóa bình luận chính
    }

	public SinhVien getById(Integer id) {
        return sinhVienRepository.findById(id).orElse(null);
    }
	
//	public List<BinhLuan> getBinhLuanByBaiViet(Integer baiVietId) {
//	    return binhLuanRepository.findByBaiVietHuongNghiep_Id(baiVietId);
//	}

	
    public BaiVietHuongNghiep getBaiVietById(Integer id) {
        return baiVietHuongNghiepRepository.findById(id).orElse(null);
    }
    
 //--------------------------- Đơn Ứng Tuyển ----------------------------//
    
    public List<BaiDangTuyenDung> getBaiTuyenDungDaDuyet() {
        // Truy vấn các bài tuyển dụng có trạng thái 'DA_DUYET'
        return baiDangTuyenDungRepository.findByTrangThai(TrangThaiBaiDang.DA_DUYET);
    }
    
    
    @Transactional
    public DonUngTuyen createDonUngTuyen(int sinhVienId, int baiDangId) {
        Optional<SinhVien> optionalSinhVien = sinhVienRepository.findById(sinhVienId);
        Optional<BaiDangTuyenDung> optionalBaiDang = baiDangTuyenDungRepository.findById(baiDangId);

        if (optionalSinhVien.isEmpty()) {
            throw new IllegalArgumentException("Sinh viên không tồn tại.");
        }
        if (optionalBaiDang.get().getTrangThai() != TrangThaiBaiDang.DA_DUYET) {
            throw new IllegalArgumentException("Bài đăng tuyển dụng chưa được duyệt.");
        }
        if (optionalBaiDang.isEmpty()) {
            throw new IllegalArgumentException("Bài đăng tuyển dụng không tồn tại.");
        }
        
        if (donUngTuyenRepository.existsBySinhVien_IdSinhVienAndBaiDangTuyenDung_IdBaiDang(sinhVienId, baiDangId)) {
            throw new IllegalArgumentException("Bạn đã ứng tuyển bài đăng này rồi.");
        }

        DonUngTuyen donUngTuyen = new DonUngTuyen();
        donUngTuyen.setSinhVien(optionalSinhVien.get());
        donUngTuyen.setBaiDangTuyenDung(optionalBaiDang.get());
        donUngTuyen.setTrangThai("Chờ duyệt");
        
        donUngTuyen.setNhaTuyenDung(optionalBaiDang.get().getNhaTuyenDung());
        
        DonUngTuyen savedDon = donUngTuyenRepository.save(donUngTuyen);
        taoThongBaoUngTuyen(savedDon); // Gửi thông báo cho nhà tuyển dụng
        return savedDon;
    }
    
    @Transactional
    private void taoThongBaoUngTuyen(DonUngTuyen donUngTuyen) {
        ThongBao thongBao = new ThongBao();
        thongBao.setIdNguoiNhan(donUngTuyen.getBaiDangTuyenDung().getNhaTuyenDung().getIdNhaTuyenDung());
        thongBao.setLoaiNguoiNhan(ThongBao.LoaiNguoiNhan.NHA_TUYEN_DUNG);
        thongBao.setNoiDung("Sinh viên " + donUngTuyen.getSinhVien().getHoTen() +
                " vừa ứng tuyển vào bài đăng: " + donUngTuyen.getBaiDangTuyenDung().getTieuDe());
        thongBaoRepository.save(thongBao);
    }

    // Xóa đơn ứng tuyển (nếu sinh viên muốn hủy)
    @Transactional
    public void deleteDonUngTuyen(int sinhVienId, int idDon) {
        Optional<DonUngTuyen> optionalDon = donUngTuyenRepository.findById(idDon);

        if (optionalDon.isEmpty()) {
            throw new IllegalArgumentException("Đơn ứng tuyển không tồn tại.");
        }

        DonUngTuyen donUngTuyen = optionalDon.get();

        // Chỉ sinh viên sở hữu đơn mới được xóa
        if (donUngTuyen.getSinhVien().getIdSinhVien() != sinhVienId) {
            throw new IllegalArgumentException("Bạn không có quyền xóa đơn này.");
        }

        donUngTuyenRepository.deleteById(idDon);
    }
    
    public List<DonUngTuyen> getDonUngTuyenBySinhVien(int sinhVienId) {
        return donUngTuyenRepository.findBySinhVien_IdSinhVien(sinhVienId);
    }
}
