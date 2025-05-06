package com.example.dai_nam.controller;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.io.IOException;
import java.net.MalformedURLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.dai_nam.model.BaiDangTuyenDung;
import com.example.dai_nam.model.BaiVietHuongNghiep;
import com.example.dai_nam.model.DonUngTuyen;
import com.example.dai_nam.model.NhaTuyenDung;
import com.example.dai_nam.model.SinhVien;
import com.example.dai_nam.repository.BaiDangTuyenDungRepository;
import com.example.dai_nam.repository.DonUngTuyenRepository;
import com.example.dai_nam.repository.SinhVienRepository;
import com.example.dai_nam.service.SinhVienService;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/SinhVien")
public class SinhVienController {
	
	 @Autowired
	 private SinhVienRepository sinhVienRepository;

	 @Autowired
	 private BaiDangTuyenDungRepository baiDangTuyenDungRepository;

	 @Autowired
	 private DonUngTuyenRepository donUngTuyenRepository;

    @Autowired
    private SinhVienService sinhVienService;
    
    private final Path uploadPath = Paths.get("uploads/banner");
    
    private final Path cvUploadDir = Paths.get("uploads/cv");

    
    @GetMapping("/NhaTuyenDung")
    public ResponseEntity<List<NhaTuyenDung>> getAllNhaTuyenDung() {
        return ResponseEntity.ok(sinhVienService.getAllNhaTuyenDung());
    }
    
    @GetMapping("/BaiVietHuongNghiep")
    public ResponseEntity<List<BaiVietHuongNghiep>> getAllBaiVietHuongNghiep(){
    	return ResponseEntity.ok(sinhVienService.getAllBaiVietHuongNghiep());
    }
    
    @GetMapping("/company_logos/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        try {
            // Đường dẫn tới tệp ảnh
            Path filePath = Paths.get("uploads/company_logos/").resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            // Kiểm tra nếu tài nguyên tồn tại và có thể đọc được
            if (resource.exists() || resource.isReadable()) {
                // Phân tích phần mở rộng của file để trả về MediaType phù hợp
                String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
                MediaType mediaType = getMediaType(fileExtension);
                
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    private MediaType getMediaType(String fileExtension) {
        switch (fileExtension) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM; // Mặc định nếu không phải hình ảnh
        }
    }
    
    @GetMapping("/NhaTuyenDung/{id}")
    public ResponseEntity<?> getNhaTuyenDungById(@PathVariable Integer id) {
        NhaTuyenDung nhaTuyenDung = sinhVienService.getNhaTuyenDungById(id);
        if (nhaTuyenDung == null) {
            return ResponseEntity.status(404)
                    .body("Không tìm thấy nhà tuyển dụng có ID: " + id);
        }
        return ResponseEntity.ok(nhaTuyenDung); 
    }
    
//--------------Bài Đăng Tuyển Dụng ---------------------//
    
    @GetMapping("/bai-dang")
    public List<BaiDangTuyenDung> getAllApprovedPosts() {
        return sinhVienService.getBaiTuyenDungDaDuyet();
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
    
    @GetMapping("/ung-tuyen")
    public ResponseEntity<?> getLichSuUngTuyen(@RequestParam int sinhVienId) {
        try {
            List<DonUngTuyen> ds = sinhVienService.getDonUngTuyenBySinhVien(sinhVienId);
            return ResponseEntity.ok(ds);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    
    @PostMapping(value = "/ung-tuyen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDonUngTuyen(
            @RequestParam int sinhVienId,
            @RequestParam int baiDangId,
            @RequestParam(value = "duongDanCv", required = false) MultipartFile duongDanCv) {

        try {
            // --- 1. Validate và lưu file CV ---
            String fileName = null;
            if (duongDanCv != null && !duongDanCv.isEmpty()) {
                // Lấy tên file sạch
                String cleanName = Paths.get(duongDanCv.getOriginalFilename())
                                        .getFileName().toString();
                fileName = "cv_" + sinhVienId + "_" + System.currentTimeMillis() + "_" + cleanName;

                // Tạo thư mục nếu chưa có
                if (Files.notExists(cvUploadDir)) {
                    Files.createDirectories(cvUploadDir);
                }

                // Lưu file, ghi đè nếu đã tồn tại
                Path target = cvUploadDir.resolve(fileName);
                Files.copy(duongDanCv.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            }

            // --- 2. Lấy entity SinhVien & BaiDang ---
            SinhVien sv = sinhVienRepository.findById(sinhVienId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với ID: " + sinhVienId));

            BaiDangTuyenDung post = baiDangTuyenDungRepository.findById(baiDangId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài đăng với ID: " + baiDangId));

            // Nếu bạn chỉ cho phép ứng tuyển bài đã duyệt
            if (post.getTrangThai() != BaiDangTuyenDung.TrangThaiBaiDang.DA_DUYET) {
                return ResponseEntity.badRequest().body("Bài đăng chưa được duyệt.");
            }

            // Kiểm tra đã ứng tuyển chưa
            boolean already = donUngTuyenRepository
              .existsBySinhVien_IdSinhVienAndBaiDangTuyenDung_IdBaiDang(sinhVienId, baiDangId);
            if (already) {
                return ResponseEntity.badRequest().body("Bạn đã ứng tuyển bài này rồi.");
            }

            // --- 3. Tạo và lưu DonUngTuyen ---
            DonUngTuyen don = new DonUngTuyen();
            don.setSinhVien(sv);
            don.setBaiDangTuyenDung(post);
            don.setNhaTuyenDung(post.getNhaTuyenDung());
            don.setNgayUngTuyen(new Timestamp(System.currentTimeMillis()));
            don.setDuongDanCv(fileName);           // đường dẫn CV
            don.setTrangThai("Chờ duyệt");          // bắt buộc phải set

            DonUngTuyen saved = donUngTuyenRepository.save(don);

            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lưu file CV: " + e.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

 


    // Xóa đơn ứng tuyển
    @DeleteMapping("/huy-ung-tuyen/{idDon}")
    public ResponseEntity<?> deleteDonUngTuyen(@RequestParam int sinhVienId, @PathVariable int idDon) {
        try {
            sinhVienService.deleteDonUngTuyen(sinhVienId, idDon);
            return ResponseEntity.ok("Hủy đơn ứng tuyển thành công.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    
    //-----------Cập nhật và xem thông tin tài khoản:
    @GetMapping("/me")
    public ResponseEntity<SinhVien> xemThongTinCaNhan(Authentication auth) {
        String email = auth.getName();
        SinhVien sv = sinhVienService.xemThongTinCaNhan(email);
        return ResponseEntity.ok(sv);
    }

    // Cập nhật thông tin của chính mình
    @PutMapping("/me")
    public ResponseEntity<SinhVien> capNhatThongTinCaNhan(
            @RequestBody SinhVien updatedSinhVien,
            Authentication auth) {
        String email = auth.getName();
        SinhVien svCapNhat = sinhVienService.updateSinhVien(email, updatedSinhVien);
        return ResponseEntity.ok(svCapNhat);
    }
    
    @GetMapping("/avatars/{fileName:.+}")
    public ResponseEntity<Resource> getAvatarSinhvVien(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads/avatars/").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
