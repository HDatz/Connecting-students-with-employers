package com.example.dai_nam.service;

import com.example.dai_nam.model.QuanTriVien;
import com.example.dai_nam.model.NhaTuyenDung;
import com.example.dai_nam.model.SinhVien;
import com.example.dai_nam.repository.QuanTriVienRepository;
import com.example.dai_nam.repository.NhaTuyenDungRepository;
import com.example.dai_nam.repository.SinhVienRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final QuanTriVienRepository quanTriVienRepository;
    private final NhaTuyenDungRepository nhaTuyenDungRepository;
    private final SinhVienRepository sinhVienRepository;

    public CustomUserDetailsService(QuanTriVienRepository quanTriVienRepository, 
                                    NhaTuyenDungRepository nhaTuyenDungRepository, 
                                    SinhVienRepository sinhVienRepository) {
        this.quanTriVienRepository = quanTriVienRepository;
        this.nhaTuyenDungRepository = nhaTuyenDungRepository;
        this.sinhVienRepository = sinhVienRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<QuanTriVien> admin = quanTriVienRepository.findByEmail(email);
        Optional<NhaTuyenDung> recruiter = nhaTuyenDungRepository.findByEmail(email);
        Optional<SinhVien> student = sinhVienRepository.findByEmail(email);

        if (admin.isPresent()) {
            return new User(admin.get().getEmail(), admin.get().getMatKhau(),
                    Collections.singletonList(() -> admin.get().getRole().name()));
        } else if (recruiter.isPresent()) {
            return new User(recruiter.get().getEmail(), recruiter.get().getMatKhau(),
                    Collections.singletonList(() -> recruiter.get().getRole().name()));
        } else if (student.isPresent()) {
            return new User(student.get().getEmail(), student.get().getMatKhau(),
                    Collections.singletonList(() -> student.get().getRole().name()));
        } else {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản với email: " + email);
        }
    }
}
