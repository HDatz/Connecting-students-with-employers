package com.example.dai_nam.controller;

import com.example.dai_nam.model.ThongBao;
import com.example.dai_nam.service.ThongBaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/thongbao")
public class ThongBaoController {

    @Autowired
    private ThongBaoService thongBaoService;

    // 1. Lấy tất cả thông báo của user
    @GetMapping("/nguoi/{id}")
    public List<ThongBao> getAllByUser(@PathVariable Integer id) {
        return thongBaoService.getThongbaoByNguoiNhan(id);
    }

    // 2. Lấy thông báo chưa đọc
    @GetMapping("/nguoi/{id}/unread")
    public List<ThongBao> getUnread(@PathVariable Integer id) {
        return thongBaoService.getUnreadThongbaoByNguoiNhan(id);
    }

    // 3. Đánh dấu 1 thông báo đã đọc
    @PostMapping("/{idThongBao}/mark-read")
    public ResponseEntity<?> markRead(@PathVariable Integer idThongBao) {
        ThongBao thongBao = thongBaoService.markAsRead(idThongBao);
        if (thongBao == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(thongBao);
    }
    
    @PostMapping("/nguoi/{id}/mark-read-all")
    public ResponseEntity<?> markAllRead(@PathVariable Integer id) {
        thongBaoService.markAllReadByNguoiNhan(id);
        return ResponseEntity.ok().build();
    }

    

}
