
package com.example.dai_nam.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "quan_tri_vien")
@Getter
@Setter
public class QuanTriVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quan_tri")
    private int idQuanTri;

    @Column(name = "ten_dang_nhap", unique = true, nullable = false, length = 50)
    private String tenDangNhap;

    @Column(name = "mat_khau", nullable = false, length = 255)
    private String matKhau;

    @Column(name = "ho_ten", length = 100)
    private String hoTen;

    @Column(name = "email", unique = true, nullable = false, length = 191)
    private String email;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "ngay_tao", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp ngayTao;

    @OneToMany(mappedBy = "tacGia", cascade = CascadeType.ALL)
    private List<BaiVietHuongNghiep> baiVietHuongNghieps;

    @OneToMany(mappedBy = "quanTriVien", cascade = CascadeType.ALL)
    private List<BinhLuan> binhLuans;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.ROLE_QUANTRIVIEN;

}