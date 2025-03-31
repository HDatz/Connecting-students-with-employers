
package com.example.dai_nam.repository;

import com.example.dai_nam.model.NhaTuyenDung;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NhaTuyenDungRepository extends JpaRepository<NhaTuyenDung, Integer> {
    // # Tìm kiếm nhà tuyển dụng theo email
    Optional<NhaTuyenDung> findByEmail(String email);

    // # Xóa nhà tuyển dụng theo email
    void deleteByEmail(String email);
    boolean existsByEmail(String email);
}