
package com.example.dai_nam.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "don_ung_tuyen")
@Setter
@Getter
public class DonUngTuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_don")
    private int idDon;

    @Column(name = "ngay_ung_tuyen", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp ngayUngTuyen;

    @Column(name = "trang_thai", nullable = false, columnDefinition = "ENUM('Chờ duyệt', 'Đã xem', 'Đã chấp nhận', 'Bị từ chối') DEFAULT 'Chờ duyệt'")
    private String trangThai;

    @Column(name = "ngay_phan_hoi")
    private Timestamp ngayPhanHoi;

    @ManyToOne
    @JoinColumn(name = "id_bai_dang", nullable = false)
    private BaiDangTuyenDung baiDangTuyenDung;

    @ManyToOne
    @JoinColumn(name = "id_sinh_vien", nullable = false)
    private SinhVien sinhVien;

    @ManyToOne
    @JoinColumn(name = "id_nha_tuyen_dung")
    private NhaTuyenDung nhaTuyenDung;

}