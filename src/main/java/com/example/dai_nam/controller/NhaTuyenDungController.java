package com.example.dai_nam.controller;

import com.example.dai_nam.model.BaiDangTuyenDung;
import com.example.dai_nam.model.DonUngTuyen;
import com.example.dai_nam.model.NhaTuyenDung;
import com.example.dai_nam.service.NhaTuyenDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nha-tuyen-dung")

public class NhaTuyenDungController {

    @Autowired
    private NhaTuyenDungService nhaTuyenDungService;

    @PutMapping("/{id}")
    public ResponseEntity<NhaTuyenDung> updateNhaTuyenDung(
            @PathVariable Integer id,
            @RequestBody NhaTuyenDung updatedNhaTuyenDung,
            @RequestParam Integer idNguoiCapNhat) {

        if (!id.equals(idNguoiCapNhat)) {
            return ResponseEntity.status(403).body(null); 
        }

        return ResponseEntity.ok(nhaTuyenDungService.updateNhaTuyenDung(idNguoiCapNhat, id, updatedNhaTuyenDung));
    }
    
    
    
    @PostMapping("/bai-dang")
    public ResponseEntity<BaiDangTuyenDung> createBaiDang(
            @RequestBody BaiDangTuyenDung baiDangTuyenDung,
            @RequestParam Integer idNguoiDang) {
    	
        System.out.println("ID Người Đăng: " + idNguoiDang);
        System.out.println("Bài Đăng Nhận Được: " + baiDangTuyenDung);
        
        if (baiDangTuyenDung.getNhaTuyenDung() == null || baiDangTuyenDung.getNhaTuyenDung().getIdNhaTuyenDung() == null) {
        	System.out.println("Lỗi: Thiếu thông tin nhà tuyển dụng!");
        	return ResponseEntity.badRequest().body(null);
        }
        
        
        Integer idNhaTuyenDung = baiDangTuyenDung.getNhaTuyenDung().getIdNhaTuyenDung();

        if (!idNhaTuyenDung.equals(idNguoiDang)) {
            return ResponseEntity.status(403).body(null);
        }

        return ResponseEntity.ok(nhaTuyenDungService.createBaiTuyenDung(baiDangTuyenDung,idNguoiDang));
    }


   
    @PutMapping("/bai-dang/{id}")
    public ResponseEntity<BaiDangTuyenDung> updateBaiDang(
    		@PathVariable int id, 
    		@RequestBody BaiDangTuyenDung baiDangTuyenDung,
    		@RequestParam int idNguoiCapNhat) {
        return ResponseEntity.ok(
        		nhaTuyenDungService.updateBaiDangTuyenDung(id, idNguoiCapNhat,baiDangTuyenDung));
    }


    @DeleteMapping("/bai-dang/{id}")
    public ResponseEntity<?> deleteBaiDang(
            @PathVariable int id,
            @RequestParam int idNguoiXoa) {
        nhaTuyenDungService.deleteBaiDangTuyenDung(id, idNguoiXoa);
        return ResponseEntity.ok("Đã xóa bài đăng tuyển dụng có ID: " + id);
    }



    @GetMapping("/{id}/bai-dang")
    public ResponseEntity<List<BaiDangTuyenDung>> getAllBaiDang(@PathVariable int id) {
        return ResponseEntity.ok(nhaTuyenDungService.getAllBaiDangTuyenDungByNhaTuyenDung(id));
    }

  
    @PutMapping("/ung-vien/{id}")
    public ResponseEntity<?> xuLyUngVien(
    		@PathVariable int id, @RequestParam boolean chapNhan, @RequestParam int idNhaTuyenDung) {
        nhaTuyenDungService.xuLyUngVien(id, idNhaTuyenDung ,chapNhan);
        return ResponseEntity.ok("Ứng viên đã được xử lý.");
    }

    
    @GetMapping("/bai-dang/{id}/ung-vien")
    public ResponseEntity<List<DonUngTuyen>> getUngVienByBaiDang(@PathVariable int id) {
        return ResponseEntity.ok(nhaTuyenDungService.getUngVienByBaiDang(id));
    }
}
