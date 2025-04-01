package com.example.dai_nam.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bai_viet_huong_nghiep")
@Data
@Setter
@Getter
public class BaiVietHuongNghiep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bai_viet") // Đảm bảo khớp với database
    private Integer idBaiViet;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @ManyToOne
    @JoinColumn(name = "id_tac_gia", referencedColumnName = "id_quan_tri", nullable = false)
    private QuanTriVien tacGia;

    @Column(name = "ngay_dang", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayDang;
}