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

    public AuthController(AuthenticationManager authenticationManager,
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
        // Xác thực cơ bản với email và password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String userType = loginRequest.getUserType(); // Ví dụ: "qt", "ntd", "sv"
        Role role = null;
        Integer userId = null;
        String ten = "";
        String email = loginRequest.getEmail();

        // Xét dựa vào loại người dùng mà chỉ tìm trong bảng tương ứng
        switch (userType.toLowerCase()) {
            case "qt":
                Optional<QuanTriVien> quanTriVienOpt = quanTriVienService.findByEmail(email);
                if (quanTriVienOpt.isPresent()) {
                    QuanTriVien qt = quanTriVienOpt.get();
                    role = qt.getRole();
                    userId = qt.getIdQuanTri();
                    ten = qt.getHoTen();
                }
                break;
            case "ntd":
                Optional<NhaTuyenDung> nhaTuyenDungOpt = nhaTuyenDungService.findByEmail(email);
                if (nhaTuyenDungOpt.isPresent()) {
                    NhaTuyenDung nt = nhaTuyenDungOpt.get();
                    role = nt.getRole();
                    userId = nt.getIdNhaTuyenDung();
                    ten = nt.getTenCongTy();
                }
                break;
            case "sv":
                Optional<SinhVien> sinhVienOpt = sinhVienService.findByEmail(email);
                if (sinhVienOpt.isPresent()) {
                    SinhVien sv = sinhVienOpt.get();
                    role = sv.getRole();
                    userId = sv.getIdSinhVien();
                    ten = sv.getHoTen();
                }
                break;
            default:
                return ResponseEntity.badRequest().body("Loại người dùng không hợp lệ");
        }
        
        if (role == null || userId == null) {
            return ResponseEntity.status(404).body("Không tìm thấy tài khoản tương ứng với loại người dùng: " + userType);
        }

        // Sinh token JWT với thông tin người dùng
        String token = jwtUtil.generateToken(email, role.name(), userId, ten);

        // Chuẩn bị dữ liệu phản hồi
        Map<String, Object> response = new HashMap<>();
        response.put("sub", email);
        response.put("role", role.name());
        response.put("id", userId);
        response.put("ten", ten);
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
