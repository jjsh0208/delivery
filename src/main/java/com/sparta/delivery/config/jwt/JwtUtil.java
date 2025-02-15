package com.sparta.delivery.config.jwt;

import com.sparta.delivery.domain.user.enums.UserRoles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long expiredMs;

    public JwtUtil(@Value("${spring.jwt.secret}") String secretKey,
                   @Value("${spring.jwt.validityInMilliseconds}") Long expiredMs) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        this.expiredMs = expiredMs;
    }

    public String getUsername(String token){
        Claims claims = parseClaims(token);
        return claims.get("username",String.class);
    }

    public String getRole(String token){
        Claims claims = parseClaims(token);
        return claims.get("role",String.class);
    }

    public String getEmail(String token){
        Claims claims = parseClaims(token);
        return claims.get("email",String.class);
    }

    public boolean isExpired(String token) {
        Claims claims = parseClaims(token); // Claims 파싱
        return claims.getExpiration().before(new Date()); // 만료 체크
    }


    public String createJwt(String username, String email ,UserRoles role){
        return Jwts.builder()
                .claim("username",username)
                .claim("email",email)
                .claim("role",role.name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .issuer("2NE1")
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    // JWT에서 Claims를 파싱하는 공통 메서드
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
