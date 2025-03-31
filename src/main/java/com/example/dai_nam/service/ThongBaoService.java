package com.example.dai_nam.service;

import com.example.dai_nam.model.ThongBao;
import com.example.dai_nam.repository.ThongBaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThongBaoService {

    @Autowired
    private ThongBaoRepository thongbaoRepository;

    // Tạo mới thông báo
    public ThongBao createThongBao(ThongBao thongbao) {
        return thongbaoRepository.save(thongbao);
    }

    // Lấy danh sách thông báo của người nhận theo idNguoiNhan
    public List<ThongBao> getThongbaoByNguoiNhan(Integer idNguoiNhan) {
        return thongbaoRepository.findByIdNguoiNhan(idNguoiNhan);
    }

    // Lấy danh sách thông báo chưa được đọc của người nhận
    public List<ThongBao> getUnreadThongbaoByNguoiNhan(Integer idNguoiNhan) {
        return thongbaoRepository.findByIdNguoiNhanAndDaXemFalse(idNguoiNhan);
    }

    // Đánh dấu thông báo đã đọc
    public ThongBao markAsRead(Integer idThongBao) {
        Optional<ThongBao> optionalThongbao = thongbaoRepository.findById(idThongBao);
        if (optionalThongbao.isPresent()) {
            ThongBao thongbao = optionalThongbao.get();
            thongbao.setDaXem(true);
            return thongbaoRepository.save(thongbao);
        }
        return null;
    }
}
