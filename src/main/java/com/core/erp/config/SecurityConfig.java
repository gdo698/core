package com.core.erp.config;

import com.core.erp.filter.JwtAuthFilter;
import com.core.erp.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;


import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로
                        .requestMatchers(
                                "/auth/login", "/auth/register",
                                "/css/**", "/js/**", "/images/**",
                                "/api/products/all", "/api/categories/tree"
                        ).permitAll()

                        // 점주 전용
                        .requestMatchers("/store/**").hasRole("OWNER")

                        // 상품관리팀 (전체 직급 가능)
                        .requestMatchers("/hq/products/**", "/api/products/**", "/api/products/upload-image").hasRole("PRD")

                        // 인사팀 승인자 (차장, 부장)만 접근
                        .requestMatchers("/hq/hr/approve/**").hasRole("HR_A")

                        // 인사팀 전체 (사원 ~ 부장)
                        .requestMatchers("/hq/hr/**").hasAnyRole("HR", "HR_A")

                        // 지점 관리 기능 (차장 이상)
                        .requestMatchers("/hq/branch/**").hasRole("BRC_E")

                        // 게시판 기능
                        .requestMatchers("/hq/board/write/**").hasRole("BRC_E") // 작성/수정/삭제
                        .requestMatchers("/hq/board/**").hasAnyRole("BRC_E", "BRC_V") // 전체 조회

                        // 공지사항, 통계 등 (점주, 본사 모두 허용)
                        .requestMatchers("/hq/notice/**", "/hq/statistics/**").hasAnyRole("OWNER", "PRD", "HR", "HR_A", "BRC_V", "BRC_E")

                        .requestMatchers("/api/store/notifications").hasAnyRole("OWNER", "PRD", "HR", "HR_A", "BRC_V", "BRC_E")

                        .requestMatchers("/store/parttimer/**").hasAnyRole("OWNER", "PRD", "HR", "HR_A", "BRC_V", "BRC_E")

                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
