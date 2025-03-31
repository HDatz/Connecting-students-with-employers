package com.example.dai_nam.controller;

import com.example.dai_nam.security.JwtUtil;
import com.example.dai_nam.service.SinhVienService;
import com.example.dai_nam.service.NhaTuyenDungService;
import com.example.dai_nam.service.QuanTriVienService;
import com.example.dai_nam.model.SinhVien;
import com.example.dai_nam.model.LoginRequest;
import com.example.dai_nam.model.NhaTuyenDung;
import com.example.dai_nam.model.QuanTriVien;
import com.example.dai_nam.model.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SinhVienService sinhVienService;
    private final NhaTuyenDungService nhaTuyenDungService;
    private final QuanTriVienService quanTriVienService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            SinhVienService sinhVienService,
            NhaTuyenDungService nhaTuyenDungService,
            QuanTriVienService quanTriVienService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.sinhVienService = sinhVienService;
        this.nhaTuyenDungService = nhaTuyenDungService;
        this.quanTriVienService = quanTriVienService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tìm người dùng trong từng bảng
        Optional<SinhVien> sinhVienOpt = sinhVienService.findByEmail(loginRequest.getEmail());
        Optional<NhaTuyenDung> nhaTuyenDungOpt = nhaTuyenDungService.findByEmail(loginRequest.getEmail());
        Optional<QuanTriVien> quanTriVienOpt = quanTriVienService.findByEmail(loginRequest.getEmail());

        Role role = null;
        Integer userId = null;
        String ten = "";

        if (quanTriVienOpt.isPresent()) {
            QuanTriVien qt = quanTriVienOpt.get();
            role = qt.getRole();
            userId = qt.getIdQuanTri();
            ten = qt.getHoTen();
        } else if (nhaTuyenDungOpt.isPresent()) {
            NhaTuyenDung nt = nhaTuyenDungOpt.get();
            role = nt.getRole();
            userId = nt.getIdNhaTuyenDung();
            ten = nt.getTenCongTy();
        } else if (sinhVienOpt.isPresent()) {
            SinhVien sv = sinhVienOpt.get();
            role = sv.getRole();
            userId = sv.getIdSinhVien();
            ten = sv.getHoTen();
        } else {
            return ResponseEntity.status(404).body("Không tìm thấy tài khoản");
        }

    
        String token = jwtUtil.generateToken(loginRequest.getEmail(), role.name(), userId, ten);

    
        Map<String, Object> response = new HashMap<>();
        response.put("sub", loginRequest.getEmail());
        response.put("role", role.name());
        response.put("id", userId);
        response.put("ten", ten);
        response.put("token", token);

        return ResponseEntity.ok(response); // Trả về toàn bộ thông tin JSON thay vì chỉ có token
    }
}