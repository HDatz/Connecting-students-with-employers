package com.example.dai_nam.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "binh_luan")
@Data
@Setter
@Getter
public class BinhLuan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_binh_luan")
    private Integer idBinhLuan;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @ManyToOne
    @JoinColumn(name = "id_sinh_vien")
    private SinhVien sinhVien;

    @ManyToOne
    @JoinColumn(name = "id_nha_tuyen_dung")
    private NhaTuyenDung nhaTuyenDung;

    @ManyToOne
    @JoinColumn(name = "id_quan_tri_vien")
    private QuanTriVien quanTriVien;

    @ManyToOne
    @JoinColumn(name = "id_bai_viet")
    private BaiVietHuongNghiep baiVietHuongNghiep;

    @ManyToOne
    @JoinColumn(name = "id_binh_luan_cha")
    private BinhLuan binhLuanCha;

    @OneToMany(mappedBy = "binhLuanCha", cascade = CascadeType.ALL)
    private List<BinhLuan> phanHois;

    @Column(nullable = false)
    private LocalDateTime ngayDang = LocalDateTime.now();
}
