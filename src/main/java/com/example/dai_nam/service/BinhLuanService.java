package com.example.dai_nam.service;

import com.example.dai_nam.model.BinhLuan;
import com.example.dai_nam.model.BaiVietHuongNghiep;
import com.example.dai_nam.model.SinhVien;
import com.example.dai_nam.model.NhaTuyenDung;
import com.example.dai_nam.repository.BinhLuanRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinhLuanService {

    @Autowired
    private BinhLuanRepository binhLuanRepository;

    // 🔹 Lấy danh sách bình luận theo bài viết
    public List<BinhLuan> getByBaiViet(BaiVietHuongNghiep baiViet) {
        return binhLuanRepository.findByBaiVietHuongNghiep(baiViet);
    }

    // 🔹 Tạo bình luận (có thể là của sinh viên hoặc nhà tuyển dụng)
    public BinhLuan taoBinhLuan(SinhVien sinhVien, NhaTuyenDung nhaTuyenDung, BinhLuan binhLuan, BaiVietHuongNghiep baiViet) {
        binhLuan.setBaiVietHuongNghiep(baiViet);
        binhLuan.setSinhVien(sinhVien);
        binhLuan.setNhaTuyenDung(nhaTuyenDung);
        return binhLuanRepository.save(binhLuan);
    }

    // 🔹 Xóa bình luận theo ID
    public void xoaBinhLuan(Integer id) {
        binhLuanRepository.deleteById(id);
    }
}
