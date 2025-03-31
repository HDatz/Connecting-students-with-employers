
package com.example.dai_nam.repository;

import com.example.dai_nam.model.SinhVien;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SinhVienRepository extends JpaRepository<SinhVien, Integer> {
    // # Tìm kiếm sinh viên theo email
    Optional<SinhVien> findByEmail(String email);

    // # Xóa sinh viên theo email
    void deleteByEmail(String email);
    boolean existsByEmail(String email);
}