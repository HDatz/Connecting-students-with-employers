
package com.example.dai_nam.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "sinh_vien")
@Getter
@Setter
public class SinhVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sinh_vien")
    private Integer idSinhVien;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "so_dien_thoai", unique = true)
    private String soDienThoai;

    @Column(name = "dia_chi")
    private String diaChi;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "ngay_sinh")
    private Date ngaySinh;

    @Column(name = "nganh_hoc")
    private String nganhHoc;

    @Column(name = "nam_tot_nghiep")
    private Integer namTotNghiep;

    @Column(name = "gioi_thieu")
    private String gioiThieu;


    @Column(name = "avatar")
    private String avatar;

    @Column(name = "ngay_tao", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date ngayTao;

    @OneToMany(mappedBy = "sinhVien", cascade = CascadeType.ALL)
    @JsonIgnore 
    private List<BinhLuan> binhLuans;

    @OneToMany(mappedBy = "sinhVien", cascade = CascadeType.ALL)
    @JsonIgnore 
    private List<DonUngTuyen> donUngTuyens;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.ROLE_SINHVIEN;

}