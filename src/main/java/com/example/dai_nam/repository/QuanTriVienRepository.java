
package com.example.dai_nam.repository;

import com.example.dai_nam.model.QuanTriVien;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuanTriVienRepository extends JpaRepository<QuanTriVien, Integer> {
    Optional<QuanTriVien> findByEmail(String email);
    boolean existsByEmail(String email);
}