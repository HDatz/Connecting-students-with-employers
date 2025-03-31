//package com.example.dai_nam.controller;
//
//import com.example.dai_nam.model.BinhLuan;
//import com.example.dai_nam.model.BaiVietHuongNghiep;
//import com.example.dai_nam.model.SinhVien;
//import com.example.dai_nam.model.NhaTuyenDung;
//import com.example.dai_nam.service.BinhLuanService;
//import com.example.dai_nam.service.NhaTuyenDungService;
//import com.example.dai_nam.service.SinhVienService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/binh-luan")
//@CrossOrigin(origins = "*")
//public class BinhLuanController {
//
//    @Autowired
//    private BinhLuanService binhLuanService;
//
//    @Autowired
//    private NhaTuyenDungService nhaTuyenDungService;
//
//    @Autowired
//    private SinhVienService sinhVienService;
//
//    // 🔹 Lấy tất cả bình luận theo bài viết
//    @GetMapping("/bai-viet/{idBaiViet}")
//    public List<BinhLuan> getBinhLuanByBaiViet(@PathVariable Integer idBaiViet) {
//        BaiVietHuongNghiep baiViet = nhaTuyenDungService.getBaiVietById(idBaiViet);
//        return binhLuanService.getByBaiViet(baiViet);
//    }
//
//    // 🔹 Sinh viên bình luận trên bài viết
//    @PostMapping("/sinh-vien/{idSinhVien}/bai-viet/{idBaiViet}")
//    public BinhLuan sinhVienBinhLuan(@PathVariable Integer idSinhVien, @PathVariable Integer idBaiViet,
//                                     @RequestBody BinhLuan binhLuan) {
//        SinhVien sinhVien = sinhVienService.getById(idSinhVien);
//        BaiVietHuongNghiep baiViet = nhaTuyenDungService.getBaiVietById(idBaiViet);
//        return binhLuanService.taoBinhLuan(sinhVien, null, binhLuan, baiViet);
//    }
//
//    // 🔹 Nhà tuyển dụng bình luận trên bài viết
//    @PostMapping("/nha-tuyen-dung/{idNTD}/bai-viet/{idBaiViet}")
//    public BinhLuan nhaTuyenDungBinhLuan(@PathVariable Integer idNTD, @PathVariable Integer idBaiViet,
//                                         @RequestBody BinhLuan binhLuan) {
//        NhaTuyenDung nhaTuyenDung = nhaTuyenDungService.getById(idNTD);
//        BaiVietHuongNghiep baiViet = nhaTuyenDungService.getBaiVietById(idBaiViet);
//        return binhLuanService.taoBinhLuan(null, nhaTuyenDung, binhLuan, baiViet);
//    }
//
//    // 🔹 Xóa bình luận theo ID
//    @DeleteMapping("/{id}")
//    public String xoaBinhLuan(@PathVariable Integer id) {
//        binhLuanService.xoaBinhLuan(id);
//        return "Xóa bình luận thành công!";
//    }
//}
