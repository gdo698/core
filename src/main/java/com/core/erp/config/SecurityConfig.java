package com.core.erp.config;

import com.core.erp.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Spring Security 설정 클래스
 * 이 클래스는 애플리케이션의 보안 설정을 담당합니다.
 * JWT 기반 인증 및 경로별 권한 설정을 정의합니다.
 */
@Configuration // 스프링 설정 클래스임을 나타냄
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor // 생성자 주입을 위한 lombok 어노테이션
public class SecurityConfig {

    // JWT 인증 필터 주입
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 보안 필터 체인 설정
     * 모든 HTTP 요청에 대한 보안 규칙을 정의합니다.
     * 
     * @param http HttpSecurity 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 보호 기능 비활성화 (REST API에서는, JWT 같은 토큰 기반 인증을 사용할 때 일반적으로 비활성화함)
            .csrf(csrf -> csrf.disable())
            
            // CORS 설정 (Cross-Origin Resource Sharing)
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOriginPatterns(List.of("*")); // 모든 출처 허용 (실제 운영 환경에서는 구체적인 도메인으로 제한하는 것이 좋음)
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
                config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
                config.setExposedHeaders(List.of("Authorization")); // 클라이언트에 노출할 헤더
                config.setAllowCredentials(true); // 인증 정보 포함 허용
                return config;
            }))
            
            // 세션 관리 설정 - JWT 사용하므로 세션은 STATELESS로 설정
            // STATELESS: 서버가 세션을 생성하지 않고 각 요청을 독립적으로 처리함
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // URL 패턴별 접근 권한 설정
            // ⚠️ 주의: 설정 순서가 중요합니다. 더 구체적인 경로를 먼저 정의하고, 일반적인 경로를 나중에 정의해야 합니다.
            .authorizeHttpRequests(auth -> auth
                // 1. 인증 없이 접근 가능한 경로 설정 (로그인, 등록, 정적 리소스 등)
                .requestMatchers(
                    "/api/auth/login", "/api/auth/register",  // 인증 관련 API
                    "/css/**", "/js/**", "/images/**",        // 정적 리소스
                    "/api/products/all", "/api/categories/tree" // 상품 정보 조회 API
                ).permitAll() // 모든 사용자 접근 허용
                
                // 2. 상품 관련 API - 여러 권한에 접근 허용
                // hasAnyRole: 여러 역할 중 하나라도 가지고 있으면 접근 허용
                .requestMatchers("/api/products/all").hasAnyRole("HQ", "HQ_HRM", "HQ_PRO", "HQ_BR", "MASTER", "STORE")
                .requestMatchers("/api/products/paged/**").hasAnyRole("HQ", "HQ_HRM", "HQ_PRO", "HQ_BR", "MASTER", "STORE")
                .requestMatchers("/api/products/**").hasAnyRole("HQ", "HQ_HRM", "HQ_PRO", "HQ_BR", "MASTER", "STORE")
                .requestMatchers("/api/products/register").hasAnyRole("HQ", "HQ_HRM", "HQ_PRO", "HQ_BR", "STORE")
                
                // 3. 점주 전용 API
                .requestMatchers("/api/store/**").hasAnyRole("STORE", "MASTER") // 점주와 마스터 권한만 접근 가능
                
                // 4. 본사 부서별 API 권한 설정
                
                // 4.1 상품관리팀 
                .requestMatchers("/api/headquarters/products/**").hasAnyRole("HQ_PRO", "HQ_PRO_M", "MASTER")
                
                // 4.2 인사팀 - 팀장은 추가 승인 권한 보유
                .requestMatchers("/api/headquarters/hr/approve/**").hasAnyRole("HQ_HRM_M", "MASTER") // 팀장급만 접근 가능
                .requestMatchers("/api/headquarters/hr/**").hasAnyRole("HQ_HRM", "HQ_HRM_M", "MASTER") // 인사팀 전체 접근 가능
                
                // 4.3 지점 관리 기능 
                .requestMatchers("/api/headquarters/branches/**").hasAnyRole("HQ_BR_M", "MASTER") // 팀장급만 접근 가능
                
                // 4.4 게시판 기능 - 모든 역할 명시적 허용
                // 게시글 GET 조회는 모든 인증된 사용자(본사 및 점주) 허용
                .requestMatchers(HttpMethod.GET, "/api/headquarters/board/**")
                    .hasAnyRole("HQ", "HQ_HRM", "HQ_HRM_M", "HQ_PRO", "HQ_PRO_M", "HQ_BR", "HQ_BR_M", "MASTER", "STORE", "NON_STORE", "NON_HQ")
                
                // 건의사항, 점포문의사항은 모든 인증된 사용자가 등록 가능
                .requestMatchers(HttpMethod.POST, "/api/headquarters/board/**")
                    .hasAnyRole("HQ", "HQ_HRM", "HQ_HRM_M", "HQ_PRO", "HQ_PRO_M", "HQ_BR", "HQ_BR_M", "MASTER", "STORE", "NON_STORE", "NON_HQ")
                
                // 공지사항 관리(등록/수정/삭제)와 답변 관리는 지점관리팀과 마스터만 가능
                .requestMatchers(HttpMethod.PUT, "/api/headquarters/board/**").hasAnyRole("HQ_BR", "HQ_BR_M", "MASTER")
                .requestMatchers(HttpMethod.DELETE, "/api/headquarters/board/**").hasAnyRole("HQ_BR", "HQ_BR_M", "MASTER")
                .requestMatchers(HttpMethod.POST, "/api/headquarters/board/comment/**").hasAnyRole("HQ_BR", "HQ_BR_M", "MASTER")
                
                // 4.5 공지사항, 통계 등 (점주, 본사 모두 허용)
                .requestMatchers("/api/headquarters/notice/**", "/api/headquarters/statistics/**")
                    .hasAnyRole("STORE", "HQ", "HQ_HRM", "HQ_PRO", "HQ_BR", "MASTER")
                
                // 5. 점포 관련 API - 구체적인 경로 지정
                .requestMatchers("/api/store/notifications/**").hasAnyRole("STORE", "MASTER")
                .requestMatchers("/api/store/parttimer/**").hasAnyRole("STORE", "MASTER")
                .requestMatchers("/api/purchase-orders/**").hasAnyRole("STORE", "MASTER")
                .requestMatchers("/api/shift-schedules/**").hasAnyRole("STORE", "MASTER")
                .requestMatchers("/api/store/**").hasAnyRole("STORE", "MASTER")
                
                // 6. MASTER 권한 모든 API 접근 설정 (전체 경로 설정)
                // ⚠️ 주의: 이 설정을 활성화하면 위의 모든 설정보다 우선 적용되어 MASTER 외 다른 역할은 접근이 제한됩니다.
                // 만약 활성화하려면 이 설정을 맨 마지막(authenticated 위)에 위치시켜야 합니다.
//                .requestMatchers("/**").hasRole("MASTER")
                
                // 7. 그 외 모든 요청은 인증 필요 (기본 설정)
                .anyRequest().authenticated() // 명시되지 않은 모든 URL은 인증된 사용자만 접근 가능
            )
            // JWT 인증 필터 추가 - UsernamePasswordAuthenticationFilter 이전에 실행되도록 설정
            // 모든 요청에서 JWT 토큰을 확인하고 인증 정보를 설정함
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 인증 관리자 빈 설정
     * JWT 토큰 기반 인증에 사용됩니다.
     * 
     * @param configuration 인증 설정
     * @return AuthenticationManager 객체
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}