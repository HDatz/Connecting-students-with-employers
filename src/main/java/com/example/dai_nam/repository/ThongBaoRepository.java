package com.example.dai_nam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dai_nam.model.ThongBao;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {

    List<ThongBao> findByIdNguoiNhan(Integer idNguoiNhan);

    List<ThongBao> findByIdNguoiNhanAndDaXemFalse(Integer idNguoiNhan);
}