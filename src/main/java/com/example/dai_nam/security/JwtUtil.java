package com.example.dai_nam.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

import com.example.dai_nam.model.Role;

import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "dsklajdslkjaslkdjlaskdjaslkdjaslkdjaslkdjlaskdjaslkjdlas";  // Ít nhất 32 ký tự
    private static final long EXPIRATION_TIME = 86400000; // 1 ngày

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)); // Chuyển thành SecretKey
    }

    public String generateToken(String email, String role, Integer userId, String ten) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)  
                .claim("id", userId)  
                .claim("ten", ten)  
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key()) 
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token); // Dùng SecretKey
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Role extractRole(String token) {
        String roleString = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
        
        return Role.valueOf(roleString); // Chuyển từ String về Enum Role
    }

}
