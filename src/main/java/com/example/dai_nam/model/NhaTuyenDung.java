
package com.example.dai_nam.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nha_tuyen_dung")
@Getter
@Setter
public class NhaTuyenDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nha_tuyen_dung")
    private Integer idNhaTuyenDung;

    @Column(name = "ten_cong_ty")
    private String tenCongTy;

    @Column(name = "email")
    private String email;

    @Column(name = "mat_khau")
    private String matKhau;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "linh_vuc")
    private String linhVuc;

    @Column(name = "trang_web")
    private String trangWeb;

    @Column(name = "mo_ta_cong_ty")
    private String moTaCongTy;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "ngay_tao")
    private Timestamp ngayTao;

    @OneToMany(mappedBy = "nhaTuyenDung", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BaiDangTuyenDung> baiDangTuyenDungs;

    @OneToMany(mappedBy = "nhaTuyenDung", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DonUngTuyen> donUngTuyens;

    @OneToMany(mappedBy = "nhaTuyenDung", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BinhLuan> binhLuans;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.ROLE_NHATUYENDUNG;
    

}
