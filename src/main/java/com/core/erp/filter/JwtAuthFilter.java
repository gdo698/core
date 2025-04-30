package com.core.erp.filter;

import com.core.erp.util.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. /auth/login 경로는 인증을 건너뛰어야 하므로 처리하지 않고 다음 필터로 넘김
        if (request.getRequestURI().equals("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 요청 헤더에서 토큰 꺼내기
        String token = resolveToken(request);
        System.out.println("==== JwtAuthFilter ====");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authorization Header: " + request.getHeader("Authorization"));
        System.out.println("Extracted Token: " + token);

        // 3. 토큰이 존재하고 유효하다면
        try {
            if (token != null && jwtProvider.validateToken(token)) {
                System.out.println("JWT 토큰 유효성 검사 성공");
                // 토큰에서 정보 추출
                Claims claims = jwtProvider.getClaims(token);

                String loginId = claims.get("loginId", String.class);
                String role = claims.get("role", String.class);
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                // 4. 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginId, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. SecurityContext에 인증 객체 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("JWT 토큰이 없거나 유효하지 않음");
            }
        } catch (Exception e) {
            System.out.println("JWT 인증 실패: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "JWT 유효성 검사 실패");
            return;
        }

        // 6. 다음 필터로 넘기기
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 Authorization Bearer 토큰 꺼내는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후만 자름
        }
        return null;
    }
}
