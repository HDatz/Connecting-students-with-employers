package com.example.dai_nam.controller;

import com.example.dai_nam.model.*;
import com.example.dai_nam.service.QuanTriVienService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;


import java.util.Date;
import java.text.ParseException;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/QuanTriVien")
public class QuanTriVienController {

    @Autowired
    private QuanTriVienService quanTriVienService;
    

    // ========================== Sinh Viên ==========================
    
    @GetMapping("/SinhVien")
    public ResponseEntity<List<SinhVien>> getAllSinhVien() {
        return ResponseEntity.ok(quanTriVienService.getAllSinhVien());
    }

    @GetMapping("/SinhVien/{id}")
    public ResponseEntity<?> getSinhVienById(@PathVariable Integer id) {
        SinhVien sinhVien = quanTriVienService.getSinhVienById(id);
        if (sinhVien == null) {
            return ResponseEntity.status(404).body("Không tìm thấy sinh viên có ID: " + id);
        }
        return ResponseEntity.ok(sinhVien); 
    }

    @PostMapping("/SinhVien")
    public ResponseEntity<SinhVien> createSinhVien(@RequestParam String hoTen, @RequestParam String email,
            @RequestParam String matKhau, @RequestParam(required = false) String soDienThoai,
            @RequestParam(required = false) String diaChi, @RequestParam(required = false) MultipartFile avatar) {
        
        try {
            SinhVien sinhVien = new SinhVien();
            sinhVien.setHoTen(hoTen);
            sinhVien.setEmail(email);
            sinhVien.setMatKhau(matKhau);
            sinhVien.setSoDienThoai(soDienThoai);
            sinhVien.setDiaChi(diaChi);
        
            if (avatar != null && !avatar.isEmpty()) {
                String fileName = "sinhvien_" + email + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get("uploads/avatars/" + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, avatar.getBytes());
                sinhVien.setAvatar(fileName);
            }
        
            SinhVien savedSinhVien = quanTriVienService.createSinhVien(sinhVien);
            return ResponseEntity.ok(savedSinhVien);
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/SinhVien/{id}")
    public ResponseEntity<?> updateSinhVien(@PathVariable Integer id, @RequestParam String hoTen,
            @RequestParam String email, @RequestParam String matKhau, @RequestParam(required = false) String soDienThoai,
            @RequestParam(required = false) String diaChi, @RequestParam(required = false) MultipartFile avatar,
            @RequestParam(required = false) String ngaySinh,@RequestParam(required = false) String nganhHoc,
            @RequestParam(required = false) Integer namTotNghiep) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_QUANTRIVIEN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Chỉ quản trị viên mới có quyền sửa thông tin sinh viên.");
        }
        

        try {
            SinhVien sinhVien = quanTriVienService.getSinhVienById(id);
            if (sinhVien == null) {
                return ResponseEntity.status(404).body("Không tìm thấy sinh viên với ID: " + id);
            }

            sinhVien.setHoTen(hoTen);
            sinhVien.setEmail(email);
            if (matKhau != null && !matKhau.isEmpty()) {
                sinhVien.setMatKhau(matKhau); // Dữ liệu mật khẩu sẽ được mã hóa trong service
            }
    

            sinhVien.setNganhHoc(nganhHoc);
            sinhVien.setSoDienThoai(soDienThoai);
            sinhVien.setNamTotNghiep(namTotNghiep);
            sinhVien.setDiaChi(diaChi);
            // Cập nhật ngày sinh nếu có
            if (ngaySinh != null && !ngaySinh.isEmpty()) {
                // Chuyển đổi ngày sinh từ String sang Date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date dateOfBirth = dateFormat.parse(ngaySinh); // Chuyển từ String thành Date
                    sinhVien.setNgaySinh(dateOfBirth); // Gán vào thuộc tính ngaySinh của sinhVien
                } catch (ParseException e) {
                    return ResponseEntity.status(400).body("Ngày sinh không hợp lệ.");
                }
            }
         // Nếu có ảnh mới, xóa ảnh cũ và lưu ảnh mới
            if (avatar != null && !avatar.isEmpty()) {
                // Nếu có ảnh cũ, xóa ảnh cũ trước
                if (sinhVien.getAvatar() != null && !sinhVien.getAvatar().isEmpty()) {
                    Path oldAvatarPath = Paths.get("uploads/avatars/" + sinhVien.getAvatar());
                    try {
                        Files.deleteIfExists(oldAvatarPath);  // Xóa ảnh cũ trên server
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Lưu ảnh mới
                String fileName = "sinhvien_" + email + "_" + System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get("uploads/avatars/" + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, avatar.getBytes());
                sinhVien.setAvatar(fileName);  // Cập nhật avatar mới
            }

            SinhVien updatedSinhVien = quanTriVienService.updateSinhVien(sinhVien);
            return ResponseEntity.ok(updatedSinhVien);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi cập nhật sinh viên: " + e.getMessage());
        }
    }

    @DeleteMapping("/SinhVien/{email}")
    public ResponseEntity<?> deleteSinhVien(@PathVariable String email) {
        quanTriVienService.deleteUser(email);
        return ResponseEntity.ok("Đã xóa sinh viên với email: " + email);
    }
    

    // ========================== Nhà Tuyển Dụng ==========================

    @GetMapping("/NhaTuyenDung")
    public ResponseEntity<List<NhaTuyenDung>> getAllNhaTuyenDung() {
        return ResponseEntity.ok(quanTriVienService.getAllNhaTuyenDung());
    }
    
    @GetMapping("/NhaTuyenDung/{id}")
    public ResponseEntity<?> getNhaTuyenDungById(@PathVariable Integer id) {
        NhaTuyenDung nhaTuyenDung = quanTriVienService.getNhaTuyenDungById(id);
        if (nhaTuyenDung == null) {
            return ResponseEntity.status(404).body("Không tìm thấy nhà tuyển dụng có ID: " + id);
        }
        return ResponseEntity.ok(nhaTuyenDung); 
    }

    @PostMapping("/NhaTuyenDung")
    public ResponseEntity<NhaTuyenDung> createNhaTuyenDung(@RequestParam String tenCongTy, @RequestParam String email,
            @RequestParam String matKhau, @RequestParam(required = false) String soDienThoai,
            @RequestParam(required = false) String diaChi, @RequestParam(required = false) String linhVuc,
            @RequestParam(required = false) String trangWeb, @RequestParam(required = false) String moTaCongTy,
            @RequestParam(required = false) MultipartFile avatar) {

        try {
            NhaTuyenDung nhaTuyenDung = new NhaTuyenDung();
            nhaTuyenDung.setTenCongTy(tenCongTy);
            nhaTuyenDung.setEmail(email);
            nhaTuyenDung.setMatKhau(matKhau);
            nhaTuyenDung.setSoDienThoai(soDienThoai);
            nhaTuyenDung.setDiaChi(diaChi);
            nhaTuyenDung.setLinhVuc(linhVuc);
            nhaTuyenDung.setTrangWeb(trangWeb);
            nhaTuyenDung.setMoTaCongTy(moTaCongTy);

            if (avatar != null && !avatar.isEmpty()) {
                String fileName = "nhatuyendung_" + email + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get("uploads/avatars/" + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, avatar.getBytes());
                nhaTuyenDung.setAvatar(fileName);
            }

            NhaTuyenDung savedNhaTuyenDung = quanTriVienService.createNhaTuyenDung(nhaTuyenDung);
            return ResponseEntity.ok(savedNhaTuyenDung);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @PutMapping("/NhaTuyenDung/{id}")
    public ResponseEntity<?> updateNhaTuyenDung(@PathVariable Integer id, @RequestParam String tenCongTy,
            @RequestParam String email, @RequestParam String matKhau, @RequestParam(required = false) String soDienThoai,
            @RequestParam(required = false) String diaChi, @RequestParam(required = false) String linhVuc,
            @RequestParam(required = false) String trangWeb, @RequestParam(required = false) String moTaCongTy,
            @RequestParam(required = false) MultipartFile avatar) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_QUANTRIVIEN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Chỉ quản trị viên mới có quyền sửa thông tin nhà tuyển dụng.");
        }

        try {
            NhaTuyenDung nhaTuyenDung = quanTriVienService.getNhaTuyenDungById(id);
            if (nhaTuyenDung == null) {
                return ResponseEntity.status(404).body("Không tìm thấy nhà tuyển dụng với ID: " + id);
            }

            nhaTuyenDung.setTenCongTy(tenCongTy);
            nhaTuyenDung.setEmail(email);
            if (matKhau != null && !matKhau.isEmpty()) {
                nhaTuyenDung.setMatKhau(matKhau); // Dữ liệu mật khẩu sẽ được mã hóa trong service
            }

            nhaTuyenDung.setSoDienThoai(soDienThoai);
            nhaTuyenDung.setDiaChi(diaChi);
            nhaTuyenDung.setLinhVuc(linhVuc);
            nhaTuyenDung.setTrangWeb(trangWeb);
            nhaTuyenDung.setMoTaCongTy(moTaCongTy);

            // Nếu có ảnh mới, xóa ảnh cũ và lưu ảnh mới
            if (avatar != null && !avatar.isEmpty()) {
                // Nếu có ảnh cũ, xóa ảnh cũ trước
                if (nhaTuyenDung.getAvatar() != null && !nhaTuyenDung.getAvatar().isEmpty()) {
                    Path oldAvatarPath = Paths.get("uploads/company_logos/" + nhaTuyenDung.getAvatar());
                    try {
                        Files.deleteIfExists(oldAvatarPath);  // Xóa ảnh cũ trên server
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Lưu ảnh mới
                String fileName = "nhatuyendung_" + email + "_" + System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get("uploads/company_logos/" + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, avatar.getBytes());
                nhaTuyenDung.setAvatar(fileName);  // Cập nhật avatar mới
            }

            NhaTuyenDung updatedNhaTuyenDung = quanTriVienService.updateNhaTuyenDung(nhaTuyenDung);
            return ResponseEntity.ok(updatedNhaTuyenDung);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi cập nhật nhà tuyển dụng: " + e.getMessage());
        }
    }


    @DeleteMapping("/NhaTuyenDung/{email}")
    public ResponseEntity<?> deleteNhaTuyenDung(@PathVariable String email) {
        quanTriVienService.deleteUser(email);
        return ResponseEntity.ok("Đã xóa nhà tuyển dụng với email: " + email);
    }

    // ========================== Reset Mật Khẩu ==========================

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_QUANTRIVIEN"));
        
        if (!isAdmin) {
            return ResponseEntity.status(403).body("Chỉ quản trị viên mới có quyền reset mật khẩu.");
        }

        quanTriVienService.resetPassword(email, newPassword);
        return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công cho email: " + email);
    }

    // ========================== Bài Viết ==========================

    @PostMapping("/BaiViet")
    public ResponseEntity<BaiVietHuongNghiep> addBaiViet(@RequestBody BaiVietHuongNghiep baiViet) {
        return ResponseEntity.ok(quanTriVienService.addBaiViet(baiViet));
    }

    @PutMapping("/BaiViet/{id}")
    public ResponseEntity<BaiVietHuongNghiep> updateBaiViet(@PathVariable Integer id, @RequestBody BaiVietHuongNghiep baiViet) {
        return ResponseEntity.ok(quanTriVienService.updateBaiViet(id, baiViet));
    }

    @DeleteMapping("/BaiViet/{id}")
    public ResponseEntity<?> deleteBaiViet(@PathVariable Integer id) {
        quanTriVienService.deleteBaiViet(id);
        return ResponseEntity.ok("Đã xóa bài viết có ID: " + id);
    }

    
    // ========================== Bài Đăng ==========================

    @PutMapping("/BaiDang/{id}/approve")
    public ResponseEntity<?> approveBaiDang(@PathVariable Integer id) {
        quanTriVienService.approveBaiDangTuyenDung(id);
        return ResponseEntity.ok("Bài đăng đã được duyệt.");
    }

    @PutMapping("/BaiDang/{id}/reject")
    public ResponseEntity<?> rejectBaiDang(@PathVariable Integer id) {
        quanTriVienService.rejectBaiDangTuyenDung(id);
        return ResponseEntity.ok("Bài đăng đã bị từ chối.");
    }

    @DeleteMapping("/BaiDang/{id}")
    public ResponseEntity<?> deleteBaiDang(@PathVariable Integer id, @RequestParam Integer idNguoiXoa) {
        quanTriVienService.deleteBaiDangTuyenDung(id, idNguoiXoa);
        return ResponseEntity.ok("Đã xóa bài đăng có ID: " + id);
    }

    
    // ========================== Bình Luận ==========================

    @DeleteMapping("/BinhLuan/{id}")
    public ResponseEntity<?> deleteBinhLuan(@PathVariable Integer id) {
        quanTriVienService.deleteBinhLuan(id);
        return ResponseEntity.ok("Đã xóa bình luận có ID: " + id);
    }

    @PostMapping("/BinhLuan/{binhLuanId}/TraLoi")
    public ResponseEntity<BinhLuan> replyToComment(@PathVariable Integer binhLuanId, @RequestParam Integer adminId,
            @RequestParam String noiDung) {
        return ResponseEntity.ok(quanTriVienService.replyToComment(binhLuanId, adminId, noiDung));
    }
    

    // ========================== Avatars ==========================

    @GetMapping("/avatars/{fileName}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads/avatars/").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Nếu không xác định được
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType)) 
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(403).body(null);
        }
    }
}
