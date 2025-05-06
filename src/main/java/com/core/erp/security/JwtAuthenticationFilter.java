package com.core.erp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    
    // 인증이 필요하지 않은 경로 목록
    private final List<String> excludedPaths = Arrays.asList(
            "/api/auth/login", 
            "/api/auth/register",
            "/api/auth/check-email"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 요청 정보 로깅
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        System.out.println("===== JwtAuthenticationFilter 실행 =====");
        System.out.println("요청 URI: " + uri);
        System.out.println("요청 메서드: " + method);
        
        // 인증이 필요하지 않은 경로인지 확인
        boolean isExcludedPath = excludedPaths.stream().anyMatch(uri::equals);
        
        if (isExcludedPath) {
            System.out.println("인증이 필요 없는 경로: " + uri + " - JWT 필터 건너뜀");
            filterChain.doFilter(request, response);
            return;
        }
        
        // JWT 토큰 확인
        String token = jwtTokenProvider.resolveToken(request);
        System.out.println("추출된 토큰: " + (token != null ? "존재함" : "없음"));
        
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                System.out.println("토큰 유효성 검증 성공");
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                
                // 인증 객체 로깅
                if (auth != null) {
                    System.out.println("인증된 사용자: " + auth.getName());
                    System.out.println("사용자 권한: " + auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(", ")));
                    System.out.println("Principal 타입: " + auth.getPrincipal().getClass().getName());
                    
                    // 게시판 관련 API 호출 시 추가 로깅
                    if (uri.contains("/api/headquarters/board/")) {
                        System.out.println("게시판 API 접근 감지 - 상세 권한 확인");
                        System.out.println("ROLE_MASTER 권한 보유: " + 
                            auth.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_MASTER")));
                    }
                } else {
                    System.out.println("인증 객체 생성 실패 (auth가 null)");
                }
                
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else if (token != null) {
                System.out.println("토큰 유효성 검증 실패");
            }
        } catch (Exception e) {
            System.err.println("토큰 처리 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}