package com.example.dai_nam.service;

import com.example.dai_nam.model.DonUngTuyen;
import com.example.dai_nam.model.SinhVien;
import com.example.dai_nam.model.BaiDangTuyenDung;
import com.example.dai_nam.model.ThongBao;
import com.example.dai_nam.repository.DonUngTuyenRepository;
import com.example.dai_nam.repository.SinhVienRepository;
import com.example.dai_nam.repository.BaiDangTuyenDungRepository;
import com.example.dai_nam.repository.ThongBaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
public class DonUngTuyenService {

    @Autowired
    private DonUngTuyenRepository donUngTuyenRepository;

    @Autowired
    private SinhVienRepository sinhVienRepository;

    @Autowired
    private BaiDangTuyenDungRepository baiDangTuyenDungRepository;

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @Autowired
    private ThongBaoService thongBaoService;

    // Sinh viên tạo đơn ứng tuyển
    @Transactional
    public DonUngTuyen createDonUngTuyen(int sinhVienId, int baiDangId) {
        Optional<SinhVien> optionalSinhVien = sinhVienRepository.findById(sinhVienId);
        Optional<BaiDangTuyenDung> optionalBaiDang = baiDangTuyenDungRepository.findById(baiDangId);

        if (optionalSinhVien.isEmpty()) {
            throw new IllegalArgumentException("Sinh viên không tồn tại.");
        }

        if (optionalBaiDang.isEmpty()) {
            throw new IllegalArgumentException("Bài đăng tuyển dụng không tồn tại.");
        }

        DonUngTuyen donUngTuyen = new DonUngTuyen();
        donUngTuyen.setSinhVien(optionalSinhVien.get());
        donUngTuyen.setBaiDangTuyenDung(optionalBaiDang.get());
        donUngTuyen.setTrangThai("Chờ duyệt");

        DonUngTuyen savedDon = donUngTuyenRepository.save(donUngTuyen);
        taoThongBaoUngTuyen(savedDon); // Gửi thông báo cho nhà tuyển dụng
        return savedDon;
    }

    // Gửi thông báo khi sinh viên ứng tuyển
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

    // Nhà tuyển dụng chấp nhận hoặc từ chối đơn ứng tuyển
    @Transactional
    public void xuLyUngVien(int ungTuyenId, boolean chapNhan) {
        Optional<DonUngTuyen> optionalUngTuyen = donUngTuyenRepository.findById(ungTuyenId);
        if (optionalUngTuyen.isPresent()) {
            DonUngTuyen ungTuyen = optionalUngTuyen.get();
            ungTuyen.setTrangThai(chapNhan ? "Đã chấp nhận" : "Đã từ chối");

            // Gửi thông báo đến sinh viên
            thongBaoService.createThongBao(new ThongBao(
                    ungTuyen.getSinhVien().getIdSinhVien(),
                    ThongBao.LoaiNguoiNhan.SINH_VIEN,
                    chapNhan ? "Bạn đã được chấp nhận vào vị trí: " + ungTuyen.getBaiDangTuyenDung().getTieuDe()
                            : "Nhà tuyển dụng đã từ chối đơn ứng tuyển của bạn."));

            donUngTuyenRepository.save(ungTuyen);
        }
    }

}
