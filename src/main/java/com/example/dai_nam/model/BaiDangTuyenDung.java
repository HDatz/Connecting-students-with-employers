package com.example.dai_nam.model;

import lombok.Data;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "bai_dang_tuyen_dung")
public class BaiDangTuyenDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bai_dang")
    private Integer idBaiDang;

    @ManyToOne
    @JoinColumn(name = "id_nha_tuyen_dung", nullable = false)
    @JsonBackReference
    private NhaTuyenDung nhaTuyenDung;

    @Column(name = "tieu_de", nullable = false, length = 100)
    private String tieuDe;

    @Column(name = "mo_ta", nullable = false, columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "dia_diem", length = 100)
    private String diaDiem;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_cong_viec", nullable = false)
    private LoaiCongViec loaiCongViec;

    public enum LoaiCongViec {
        TOAN_THOI_GIAN, BAN_THOI_GIAN, THUC_TAP
    }

    @Column(name = "muc_luong", length = 50)
    private String mucLuong;

    @Column(name = "yeu_cau", columnDefinition = "TEXT")
    private String yeuCau;

    @Column(name = "ngay_dang", updatable = false, nullable = false)
    private Timestamp ngayDang;

    @Column(name = "han_nop")
    private Date hanNop;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThaiBaiDang trangThai;
    
    @Column(name = "so_luong_tuyen", nullable = false)
    private int soLuongTuyen;

    public enum TrangThaiBaiDang {
        CHO_DUYET,
        DA_DUYET,
        TU_CHOI
    }
    
    @Column(name = "banner", length = 255)
    private String banner;

    @OneToMany(mappedBy = "baiDangTuyenDung", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DonUngTuyen> donUngTuyens;

    @PrePersist
    protected void onCreate() {
        this.ngayDang = new Timestamp(System.currentTimeMillis());
    }
}
