package com.example.dai_nam.controller;

import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.dai_nam.model.BaiVietHuongNghiep;
import com.example.dai_nam.model.NhaTuyenDung;
import com.example.dai_nam.service.SinhVienService;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/SinhVien")
public class SinhVienController {

    @Autowired
    private SinhVienService sinhVienService;
    
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
}
