
package com.example.dai_nam.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idThongBao;

    @Column(nullable = false)
    private Integer idNguoiNhan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoaiNguoiNhan loaiNguoiNhan;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Column(nullable = false)
    private Boolean daXem = false;

    @Column(nullable = false)
    private LocalDateTime ngayGui = LocalDateTime.now();

    public enum LoaiNguoiNhan {
        SINH_VIEN,
        NHA_TUYEN_DUNG,
        QUAN_TRI_VIEN
    }

    @ManyToOne
    @JoinColumn(name = "id_sinh_vien")
    private SinhVien sinhVien;

    @ManyToOne
    @JoinColumn(name = "id_nha_tuyen_dung")
    private NhaTuyenDung nhaTuyenDung;

    @ManyToOne
    @JoinColumn(name = "id_quan_tri_vien")
    private QuanTriVien quanTriVien;

    public ThongBao(Integer idNguoiNhan, LoaiNguoiNhan loaiNguoiNhan, String noiDung) {
        this.idNguoiNhan = idNguoiNhan;
        this.loaiNguoiNhan = loaiNguoiNhan;
        this.noiDung = noiDung;
        this.daXem = false;
        this.ngayGui = LocalDateTime.now();
    }
}