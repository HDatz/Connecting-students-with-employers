package com.example.dai_nam.config;

import com.example.dai_nam.security.JwtFilter;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.cors(cors -> cors.configurationSource(request -> {
	        	CorsConfiguration config = new CorsConfiguration();
	            config.setAllowedOrigins(List.of("http://127.0.0.1:5500"));
	            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	            config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
	            return config;
        	}))
            .csrf(csrf -> csrf.disable()) // 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/QuanTriVien/avatars/**").permitAll()
                .requestMatchers("/api/QuanTriVien/company_logos/**").permitAll()
                .requestMatchers("/api/SinhVien/NhaTuyenDung/**").permitAll()
                .requestMatchers("/api/SinhVien/company_logos/**").permitAll()
                .requestMatchers("/api/SinhVien/BaiVietHuongNghiep/**").permitAll()
                .requestMatchers("/api/SinhVien/banners/**").permitAll() 
                .requestMatchers("/api/SinhVien/bai-dang").permitAll()
                .requestMatchers("/api/QuanTriVien/**").hasAuthority("ROLE_QUANTRIVIEN")
                .requestMatchers("/api/nha-tuyen-dung/**").hasAuthority("ROLE_NHATUYENDUNG")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // 

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
