
package com.example.dai_nam.repository;

import com.example.dai_nam.model.BaiDangTuyenDung;
import com.example.dai_nam.model.BaiDangTuyenDung.TrangThaiBaiDang;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaiDangTuyenDungRepository extends JpaRepository<BaiDangTuyenDung, Integer> {

    List<BaiDangTuyenDung> findByNhaTuyenDung_IdNhaTuyenDung(int nhaTuyenDungId);
    
    List<BaiDangTuyenDung> findByTrangThai (TrangThaiBaiDang trangThai);

    List<BaiDangTuyenDung> findByTieuDeContainingOrDiaDiemContaining(String keyword, String keyword2);

    List<BaiDangTuyenDung> findByTieuDeContaining(String keyword);

}
