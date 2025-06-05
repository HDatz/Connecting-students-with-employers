package com.example.dai_nam.controller;

import com.example.dai_nam.model.*;
import com.example.dai_nam.model.BaiDangTuyenDung.TrangThaiBaiDang;
import com.example.dai_nam.service.QuanTriVienService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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
import java.util.Map;

import org.springframework.http.HttpStatus;


import java.util.Date;
import java.util.HashMap;
import java.text.ParseException;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/QuanTriVien")
public class QuanTriVienController {

    @Autowired
    private QuanTriVienService quanTriVienService;
    
    private final Path uploadPath = Paths.get("uploads/banner");
    

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
                Path filePath = Paths.get("uploads/company_logos/" + fileName);
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
                    Path oldAvatarPath = Paths.get("uploads/conpany_logos/" + nhaTuyenDung.getAvatar());
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
    
    @GetMapping("/BaiViet")
    public ResponseEntity<List<BaiVietHuongNghiep>> getAllBaiViet() {
        return ResponseEntity.ok(quanTriVienService.getAllBaiViet());
    }

    @GetMapping("/BaiViet/{id}")
    public ResponseEntity<BaiVietHuongNghiep> getChiTietBaiViet(@PathVariable Integer id) {
        BaiVietHuongNghiep bv = quanTriVienService.getBaiVietById(id);
        return ResponseEntity.ok(bv);
    }
    
    @PostMapping("/BaiViet")
    public ResponseEntity<BaiVietHuongNghiep> addBaiViet(@RequestBody BaiVietHuongNghiep baiViet) {
    	try {
            BaiVietHuongNghiep newBaiViet = quanTriVienService.addBaiViet(baiViet);
            return ResponseEntity.ok(newBaiViet);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
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
    @GetMapping("/BaiDang/danhsach")
    public ResponseEntity<List<Map<String, Object>>> getListBaiDang() {
        List<BaiDangTuyenDung> list = quanTriVienService.getAllBaiDang();

        // Chuyển mỗi bài đăng thành 1 Map chứa đầy đủ thông tin + tên nhà tuyển dụng
        List<Map<String, Object>> response = list.stream().map(baiDang -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idBaiDang", baiDang.getIdBaiDang());
            map.put("tieuDe", baiDang.getTieuDe());
            map.put("moTa", baiDang.getMoTa());
            map.put("diaDiem", baiDang.getDiaDiem());
            map.put("loaiCongViec", baiDang.getLoaiCongViec());
            map.put("mucLuong", baiDang.getMucLuong());
            map.put("yeuCau", baiDang.getYeuCau());
            map.put("ngayDang", baiDang.getNgayDang());
            map.put("hanNop", baiDang.getHanNop());
            map.put("trangThai", baiDang.getTrangThai());
            map.put("soLuongTuyen", baiDang.getSoLuongTuyen());
            map.put("banner", baiDang.getBanner());

            if (baiDang.getNhaTuyenDung() != null) {
                map.put("tenCongTy", baiDang.getNhaTuyenDung().getTenCongTy());
            } else {
                map.put("tenCongTy", "Không rõ");
            }

            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/BaiDang/{id}")
    public ResponseEntity<Map<String, Object>> getChiTietBaiDang(@PathVariable Integer id) {
        BaiDangTuyenDung baiDang = quanTriVienService.getChiTietBaiDang(id);

        // Xây dựng URL banner dựa trên endpoint /banners/{filename}
        String bannerFilename = baiDang.getBanner(); 
        String bannerUrl = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/QuanTriVien/banners/")
            .path(bannerFilename)
            .toUriString();

        Map<String, Object> map = new HashMap<>();
        map.put("idBaiDang", baiDang.getIdBaiDang());
        map.put("tieuDe", baiDang.getTieuDe());
        map.put("moTa", baiDang.getMoTa());
        map.put("diaDiem", baiDang.getDiaDiem());
        map.put("loaiCongViec", baiDang.getLoaiCongViec());
        map.put("mucLuong", baiDang.getMucLuong());
        map.put("yeuCau", baiDang.getYeuCau());
        map.put("ngayDang", baiDang.getNgayDang());
        map.put("hanNop", baiDang.getHanNop());
        map.put("trangThai", baiDang.getTrangThai());
        map.put("soLuongTuyen", baiDang.getSoLuongTuyen());

        // Đưa URL banner thay vì chỉ filename
        map.put("bannerUrl", bannerUrl);

        if (baiDang.getNhaTuyenDung() != null) {
            map.put("tenCongTy", baiDang.getNhaTuyenDung().getTenCongTy());
            map.put("email", baiDang.getNhaTuyenDung().getEmail());
            map.put("sdt", baiDang.getNhaTuyenDung().getSoDienThoai());
            map.put("diaChi", baiDang.getNhaTuyenDung().getDiaChi());
        } else {
            map.put("tenCongTy", "Không rõ");
        }

        return ResponseEntity.ok(map);
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

    
    // Lay danh sách bài đăng chờ duyệt
    @GetMapping("/BaiDang/choduyet")
    public ResponseEntity<List<Map<String, Object>>> getBaiDangChoDuyet() {
        List<BaiDangTuyenDung> list = quanTriVienService.getBaiDangByTrangThai(TrangThaiBaiDang.CHO_DUYET);

        // Chuyển mỗi bài đăng thành 1 Map chứa đầy đủ thông tin + tên nhà tuyển dụng
        List<Map<String, Object>> response = list.stream().map(baiDang -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idBaiDang", baiDang.getIdBaiDang());
            map.put("tieuDe", baiDang.getTieuDe());
            map.put("moTa", baiDang.getMoTa());
            map.put("diaDiem", baiDang.getDiaDiem());
            map.put("loaiCongViec", baiDang.getLoaiCongViec());
            map.put("mucLuong", baiDang.getMucLuong());
            map.put("yeuCau", baiDang.getYeuCau());
            map.put("ngayDang", baiDang.getNgayDang());
            map.put("hanNop", baiDang.getHanNop());
            map.put("trangThai", baiDang.getTrangThai());
            map.put("soLuongTuyen", baiDang.getSoLuongTuyen());
            map.put("banner", baiDang.getBanner());

            if (baiDang.getNhaTuyenDung() != null) {
                map.put("tenCongTy", baiDang.getNhaTuyenDung().getTenCongTy());
            } else {
                map.put("tenCongTy", "Không rõ");
            }

            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/BaiDang/{id}/choduyet")
    public ResponseEntity<?> duaLaiChoDuyet(@PathVariable Integer id) {
        quanTriVienService.duaLaiChoDuyetBaiDangTuyenDung(id);
        return ResponseEntity.ok("Đã đưa bài đăng trở lại trạng thái chờ duyệt.");
    }
    
    // ✅ Duyệt bài đăng
    @PutMapping("/BaiDang/{id}/duyet")
    public ResponseEntity<?> duyetBaiDang(@PathVariable Integer id) {
        quanTriVienService.duyetBaiDangTuyenDung(id);
        return ResponseEntity.ok("Bài đăng đã được duyệt.");
    }

    // ✅ Từ chối bài đăng
    @PutMapping("/BaiDang/{id}/tuchoi")
    public ResponseEntity<?> tuChoiBaiDang(@PathVariable Integer id) {
        quanTriVienService.tuchoiBaiDangTuyenDung(id);
        return ResponseEntity.ok("Bài đăng đã bị từ chối.");
    }

    // ✅ Xóa bài đăng
    @DeleteMapping("/BaiDang/{id}")
    public ResponseEntity<?> xoaBaiDang(@PathVariable Integer id, @RequestParam Integer idNguoiXoa) {
        quanTriVienService.xoaBaiDangTuyenDung(id, idNguoiXoa);
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

    
    // ========================== Biểu Đồ ==========================
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalSinhVien", quanTriVienService.getTotalSinhVien());
        stats.put("totalNhaTuyenDung", quanTriVienService.getTotalNhaTuyenDung());
        stats.put("totalBinhLuan", quanTriVienService.getTotalBinhLuan());
        stats.put("totalBaiVietHuongNghiep", quanTriVienService.getTotalBaiVietHuongNghiep());
        stats.put("totalBaiTuyenDung", quanTriVienService.getTotalBaiTuyenDung());
        
        return ResponseEntity.ok(stats);
    }
}
