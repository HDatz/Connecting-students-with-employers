package com.example.dai_nam.repository;

import com.example.dai_nam.model.BinhLuan;
import com.example.dai_nam.model.BaiVietHuongNghiep;
import com.example.dai_nam.model.SinhVien;
import com.example.dai_nam.model.NhaTuyenDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinhLuanRepository extends JpaRepository<BinhLuan, Integer> {

    // 🔹 Lấy danh sách bình luận theo bài viết
    List<BinhLuan> findByBaiVietHuongNghiep(BaiVietHuongNghiep idBaiViet);

    // 🔹 Lấy danh sách bình luận của một sinh viên
    List<BinhLuan> findBySinhVien(SinhVien sinhVien);

    // 🔹 Lấy danh sách bình luận của một nhà tuyển dụng
    List<BinhLuan> findByNhaTuyenDung(NhaTuyenDung nhaTuyenDung);

    // 🔹 Lấy danh sách bình luận cha (bình luận gốc, không phải phản hồi)
    List<BinhLuan> findByBinhLuanChaIsNull();
    
//    List<BinhLuan> findByBaiVietHuongNghiep_Id(Integer idBaiViet);


}
