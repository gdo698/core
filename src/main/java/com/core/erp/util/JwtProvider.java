package com.core.erp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private Key key;
    private final long tokenValidTime = 1000L * 60 * 60 * 3; // 3시간 (3 * 60 * 60 * 1000)

    @PostConstruct
    protected void init() {
        // secretKey가 빈 값이면 자동으로 강력한 키를 생성
        if (secretKey == null || secretKey.isEmpty()) {
            // 비어 있으면 안전한 256비트 키 생성
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);  // 안전한 키 자동 생성
        } else {
            this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        }
    }

    // 토큰 생성
    public String createToken(String loginId, String role, String userType,
                              Integer storeId, String name, String branchName) {
        Claims claims = Jwts.claims();
        claims.put("loginId", loginId);
        claims.put("role", role);
        claims.put("userType", userType);
        claims.put("storeId", storeId);
        claims.put("name", name);
        claims.put("branchName", branchName);


        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 클레임(정보) 추출
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);  // 파싱이 되면 유효한 토큰
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
