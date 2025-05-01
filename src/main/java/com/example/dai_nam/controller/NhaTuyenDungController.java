package com.example.dai_nam.controller;

import com.example.dai_nam.model.BaiDangTuyenDung;
import com.example.dai_nam.model.DonUngTuyen;
import com.example.dai_nam.model.NhaTuyenDung;
import com.example.dai_nam.service.NhaTuyenDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


@RestController
@RequestMapping("/api/nha-tuyen-dung")
public class NhaTuyenDungController {

    @Autowired 
    private NhaTuyenDungService nhaTuyenDungService;

    // đường dẫn gốc để lưu banner, config trong application.properties:
    // file.upload-dir=uploads/banner
    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() throws IOException {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
    }

    // --- Cập nhật thông tin nhà tuyển dụng ---
    @PutMapping("/{id}")
    public ResponseEntity<NhaTuyenDung> updateNhaTuyenDung(
            @PathVariable Integer id,
            @RequestBody NhaTuyenDung updated,
            @RequestParam Integer idNguoiCapNhat) {

        if (!id.equals(idNguoiCapNhat)) {
            return ResponseEntity.status(403).build();
        }
        NhaTuyenDung saved = nhaTuyenDungService
            .updateNhaTuyenDung(idNguoiCapNhat, id, updated);
        return ResponseEntity.ok(saved);
    }

    // --- Tạo bài đăng mới (upload banner nếu có) ---
    @PostMapping(
        value = "/bai-dang", 
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BaiDangTuyenDung> createBaiDang(
            @RequestParam String tieuDe,
            @RequestParam String moTa,
            @RequestParam String diaDiem,
            @RequestParam BaiDangTuyenDung.LoaiCongViec loaiCongViec,
            @RequestParam String mucLuong,
            @RequestParam String yeuCau,
            @RequestParam 
              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date hanNop,
            @RequestParam int soLuongTuyen,
            @RequestParam String email,
            @RequestParam int idNguoiDang,
            @RequestParam(value = "banner", required = false) MultipartFile banner
    ) {
        // 1. Chuẩn bị entity
        BaiDangTuyenDung bai = new BaiDangTuyenDung();
        bai.setTieuDe(tieuDe);
        bai.setMoTa(moTa);
        bai.setDiaDiem(diaDiem);
        bai.setLoaiCongViec(loaiCongViec);
        bai.setMucLuong(mucLuong);
        bai.setYeuCau(yeuCau);
        bai.setHanNop(hanNop);
        bai.setSoLuongTuyen(soLuongTuyen);

        // 2. Lưu banner (nếu có)
        if (banner != null && !banner.isEmpty()) {
            String cleanName = StringUtils.cleanPath(banner.getOriginalFilename());
            String fileName = "baidang_" + email + "_" + System.currentTimeMillis() + "_" + cleanName;
            try {
                Path target = uploadPath.resolve(fileName);
                Files.copy(
                    banner.getInputStream(), 
                    target, 
                    StandardCopyOption.REPLACE_EXISTING
                );
                bai.setBanner(fileName);
            } catch (IOException ex) {
                throw new RuntimeException("Lỗi khi lưu banner: " + ex.getMessage(), ex);
            }
        }

        // 3. Gọi service
        BaiDangTuyenDung saved = nhaTuyenDungService
            .createBaiTuyenDung(bai, idNguoiDang);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/banners/{filename:.+}")
    public ResponseEntity<Resource> getBanner(@PathVariable String filename) {
        try {
            Path file = uploadPath.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- Cập nhật bài đăng (có thể đổi banner) ---
    @PutMapping(
      value = "/bai-dang/{id}", 
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BaiDangTuyenDung> updateBaiDang(
            @PathVariable int id,
            @RequestParam String tieuDe,
            @RequestParam String moTa,
            @RequestParam String diaDiem,
            @RequestParam BaiDangTuyenDung.LoaiCongViec loaiCongViec,
            @RequestParam String mucLuong,
            @RequestParam String yeuCau,
            @RequestParam 
              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date hanNop,
            @RequestParam int soLuongTuyen,
            @RequestParam String email,
            @RequestParam int idNguoiCapNhat,
            @RequestParam(value = "banner", required = false) MultipartFile banner
    ) {
        // 1. Tái tạo entity với các field mới
        BaiDangTuyenDung updated = new BaiDangTuyenDung();
        updated.setTieuDe(tieuDe);
        updated.setMoTa(moTa);
        updated.setDiaDiem(diaDiem);
        updated.setLoaiCongViec(loaiCongViec);
        updated.setMucLuong(mucLuong);
        updated.setYeuCau(yeuCau);
        updated.setHanNop(hanNop);
        updated.setSoLuongTuyen(soLuongTuyen);

        // 2. Xử lý banner cũ → xóa + lưu mới
        if (banner != null && !banner.isEmpty()) {
            // Lấy banner cũ từ DB
            BaiDangTuyenDung old = nhaTuyenDungService.getBaiDangById(id);
            String oldFile = old.getBanner();
            if (oldFile != null) {
                try { Files.deleteIfExists(uploadPath.resolve(oldFile)); }
                catch (IOException ignored) {}
            }
            // Lưu file mới
            String cleanName = StringUtils.cleanPath(banner.getOriginalFilename());
            String fileName = "baidang_" + email + "_" + System.currentTimeMillis() + "_" + cleanName;
            try {
                Path target = uploadPath.resolve(fileName);
                Files.copy(
                    banner.getInputStream(), 
                    target, 
                    StandardCopyOption.REPLACE_EXISTING
                );
                updated.setBanner(fileName);
            } catch (IOException ex) {
                throw new RuntimeException("Lỗi khi lưu banner mới: " + ex.getMessage(), ex);
            }
        }

        // 3. Gọi service để save
        BaiDangTuyenDung saved = nhaTuyenDungService
            .updateBaiDangTuyenDung(id, idNguoiCapNhat, updated);
        return ResponseEntity.ok(saved);
    }

    // --- Xóa bài đăng ---
    @DeleteMapping("/bai-dang/{id}")
    public ResponseEntity<?> deleteBaiDang(
            @PathVariable int id,
            @RequestParam int idNguoiXoa) {
        nhaTuyenDungService.deleteBaiDangTuyenDung(id, idNguoiXoa);
        return ResponseEntity.ok("Đã xóa bài đăng có ID: " + id);
    }

    // --- Lấy tất cả bài đăng của NTD ---
    @GetMapping("/{id}/bai-dang")
    public ResponseEntity<List<BaiDangTuyenDung>> getAllBaiDang(
            @PathVariable int id,
            @RequestParam(name = "trangThai", required = false) BaiDangTuyenDung.TrangThaiBaiDang trangThai) {
        
        List<BaiDangTuyenDung> baiDangList = nhaTuyenDungService.getAllBaiDangTuyenDungByNhaTuyenDung(id, trangThai);
        
        return ResponseEntity.ok(baiDangList);
    }

    // --- Xử lý ứng viên ---
    @PutMapping("/ung-vien/{id}")
    public ResponseEntity<?> xuLyUngVien(
            @PathVariable int id,
            @RequestParam boolean chapNhan,
            @RequestParam int idNhaTuyenDung) {
        nhaTuyenDungService.xuLyUngVien(id, idNhaTuyenDung, chapNhan);
        return ResponseEntity.ok("Ứng viên đã được xử lý.");
    }
    
    //--------------Xoa Đơn---------------//
    @DeleteMapping("/ung-vien/{id}")
    public ResponseEntity<?> xoaUngVien(
            @PathVariable("id") int idDon,
            @RequestParam("idNhaTuyenDung") int idNhaTuyenDung) {
        nhaTuyenDungService.xoaDonUngTuyen(idDon, idNhaTuyenDung);
        return ResponseEntity.ok("Xóa đơn ứng tuyển thành công.");
    }


    // --- Lấy ứng viên theo bài đăng ---
    @GetMapping("/bai-dang/{id}/ung-vien")
    public ResponseEntity<List<DonUngTuyen>> getUngVienByBaiDang(
            @PathVariable int id) {
        return ResponseEntity.ok(
            nhaTuyenDungService.getUngVienByBaiDang(id)
        );
    }
    
    @GetMapping("/cv/{filename:.+}")
    public ResponseEntity<Resource> getCvFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/cv").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    
    
    
}
