package com.core.erp.config;

import com.core.erp.filter.JwtAuthFilter;
import com.core.erp.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화 (REST API에서는 비활성화 추천)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("*")); // 모든 도메인에서 접근 허용
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
                    config.setAllowCredentials(true); // 쿠키 정보 포함 허용
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login", "/auth/register", "/css/**", "/js/**", "/images/**", // 로그인, 회원가입, 정적 파일
                                "/headquarters/**", "/store/**", // 본사 ERP와 점주 ERP 경로
                                "/api/products/all", "/api/products/**", // 상품 관련 API
                                "/api/products/detail/**", "/api/categories/tree", "/api/products/upload-image",
                                "/api/products/edit/**", "/api/storeStock/**", "/api/storeStock/all",
                                "/api/storeStock/detail/**" // 상품 수정, 재고 관련 API
                        ).permitAll()  // 위의 경로들은 모두 인증 없이 접근 허용
                        .requestMatchers("/store/**").hasAnyRole("OWNER", "HQ")  // 점주와 본사 경로는 모두 허용
                        .requestMatchers("/hq/**").hasRole("HQ") // 본사 관련 경로는 "HQ" 권한만 허용
                        .requestMatchers("/hq/board/**", "/hq/notice/**", "/hq/statistics/**").hasAnyRole("HQ", "OWNER") // 본사의 게시판, 공지사항 등은 "HQ" 또는 "OWNER" 권한만 허용
                        .anyRequest().authenticated()  // 나머지 경로는 모두 인증이 필요
                )
                .addFilterBefore(new JwtAuthFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);  // JWT 필터를 UsernamePasswordAuthenticationFilter 전에 추가

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 비밀번호 암호화에 BCrypt 사용
    }
}