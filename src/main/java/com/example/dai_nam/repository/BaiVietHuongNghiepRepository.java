package com.example.dai_nam.repository;

import com.example.dai_nam.model.BaiVietHuongNghiep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaiVietHuongNghiepRepository extends JpaRepository<BaiVietHuongNghiep, Integer> {

}
