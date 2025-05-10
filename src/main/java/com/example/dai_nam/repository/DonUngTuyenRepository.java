package com.example.dai_nam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dai_nam.model.DonUngTuyen;

@Repository
public interface DonUngTuyenRepository extends JpaRepository<DonUngTuyen, Integer> {
    List<DonUngTuyen> findByBaiDangTuyenDung_IdBaiDang(Integer baiDangId);
    
    boolean existsBySinhVien_IdSinhVienAndBaiDangTuyenDung_IdBaiDang(Integer sinhVienId, Integer baiDangId);

 // Lấy tất cả đơn ứng tuyển của 1 sinh viên
	List<DonUngTuyen> findBySinhVien_IdSinhVien(Integer sinhVienId);
	
	boolean existsByBaiDangTuyenDung_IdBaiDang(int baiDangId);

}