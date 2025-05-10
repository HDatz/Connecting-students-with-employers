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
import org.springframework.security.core.Authentication;
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
    @PutMapping("/{idNhaTuyenDung}")
    public ResponseEntity<NhaTuyenDung> capNhatCongTy(
            @PathVariable Integer idNhaTuyenDung,
            @RequestBody NhaTuyenDung updatedNhaTuyenDung,
            Authentication auth) {

        // lấy id người đang login từ Authentication
        String email = auth.getName();
        NhaTuyenDung current = nhaTuyenDungService.xemThongTinCongTy(email);
        Integer idNguoiCapNhat = current.getIdNhaTuyenDung();

        NhaTuyenDung saved = nhaTuyenDungService
                .updateNhaTuyenDung(idNguoiCapNhat, idNhaTuyenDung, updatedNhaTuyenDung);

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
    
    @GetMapping("/bai-dang/{id}")
    public ResponseEntity<?> getChiTietBaiDang(
            @PathVariable("id") int idBaiDang,
            @RequestParam("idNhaTuyenDung") int idNhaTuyenDung) {
        try {
            BaiDangTuyenDung detail = nhaTuyenDungService
                    .getChiTietBaiDang(idBaiDang, idNhaTuyenDung);
            return ResponseEntity.ok(detail);
        } catch (Exception ex) {
            if (ex instanceof AccessDeniedException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền truy cập bài đăng này");
            } else if (ex instanceof IllegalArgumentException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy bài đăng");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống");
            }
        }
    }
    
    @GetMapping("/company")
    public ResponseEntity<NhaTuyenDung> xemThongTinCongTy(Authentication auth) {
        String email = auth.getName();
        NhaTuyenDung ntd = nhaTuyenDungService.xemThongTinCongTy(email);
        return ResponseEntity.ok(ntd);
    }
    
    @GetMapping("/company_logos/{fileName}")
    public ResponseEntity<Resource> getCompanyLogo(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads/company_logos/").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // Kiểm tra xem ảnh có tồn tại và có thể đọc được không
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Xác định loại nội dung của tệp
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Nếu không xác định được
            }

            // Trả về ảnh với đúng kiểu nội dung
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(403).body(null);
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
    	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date hanNop,
    	        @RequestParam int soLuongTuyen,
    	        @RequestParam String email,
    	        @RequestParam int idNguoiCapNhat,
    	        @RequestParam(value = "banner", required = false) MultipartFile banner
    	) {
    	    // 1. Lấy bài đăng cũ
    	    BaiDangTuyenDung existing = nhaTuyenDungService.getBaiDangById(id);
    	    if (existing == null) {
    	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    	    }

    	    // 2. Cập nhật các trường thông tin
    	    existing.setTieuDe(tieuDe);
    	    existing.setMoTa(moTa);
    	    existing.setDiaDiem(diaDiem);
    	    existing.setLoaiCongViec(loaiCongViec);
    	    existing.setMucLuong(mucLuong);
    	    existing.setYeuCau(yeuCau);
    	    existing.setHanNop(hanNop);
    	    existing.setSoLuongTuyen(soLuongTuyen);

    	    // 3. Xử lý banner
    	    if (banner != null && !banner.isEmpty()) {
    	        // xóa banner cũ
    	        String oldFile = existing.getBanner();
    	        if (oldFile != null) {
    	            try {
    	                Files.deleteIfExists(uploadPath.resolve(oldFile));
    	            } catch (IOException ignored) {}
    	        }
    	        // lưu banner mới
    	        String cleanName = StringUtils.cleanPath(banner.getOriginalFilename());
    	        String fileName = "baidang_" + email + "_" + System.currentTimeMillis() + "_" + cleanName;
    	        try {
    	            Path target = uploadPath.resolve(fileName);
    	            Files.copy(banner.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
    	            existing.setBanner(fileName);
    	        } catch (IOException ex) {
    	            throw new RuntimeException("Lỗi khi lưu banner mới: " + ex.getMessage(), ex);
    	        }
    	    }
    	    // nếu không có banner mới, existing.getBanner() vẫn là banner cũ

    	    // 4. Gọi service để lưu
    	    BaiDangTuyenDung saved = nhaTuyenDungService.updateBaiDangTuyenDung(id, idNguoiCapNhat, existing);
    	    return ResponseEntity.ok(saved);
    }


    // --- Xóa bài đăng ---
    @DeleteMapping("/bai-dang/{idBaiDang}/xoa")
    public ResponseEntity<?> xoaBaiDangTuyenDung(
            @PathVariable int idBaiDang,
            @RequestParam int idNguoiXoa) {

        try {
            nhaTuyenDungService.deleteBaiDangTuyenDung(idBaiDang, idNguoiXoa);
            return ResponseEntity.ok("Xóa bài đăng thành công.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi xóa bài đăng.");
        }
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
