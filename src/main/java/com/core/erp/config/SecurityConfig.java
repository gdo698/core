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
     * 모든 HTTP 요청에 대한 보안 규칙을 정의합니다.email_token
     * 
     * @param http HttpSecurity 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("==== SecurityConfig 설정 로드 중 ====");
        
        http
            // CSRF 보호 기능 비활성화 (REST API에서는, JWT 같은 토큰 기반 인증을 사용할 때 일반적으로 비활성화함)
            .csrf(csrf -> csrf.disable())
            
            // CORS 설정 (Cross-Origin Resource Sharing)
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                // 명시적 도메인 목록 설정
                config.setAllowedOrigins(List.of(
                    "http://localhost:3000", 
                    "http://localhost:8080", 
                    "http://127.0.0.1:3000", 
                    "http://127.0.0.1:8080"
                )); // 특정 출처 허용
                config.setAllowedMethods(List.of("GET","PATCH" ,"POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
                config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
                config.setExposedHeaders(List.of("Authorization")); // 클라이언트에 노출할 헤더
                config.setAllowCredentials(true); // 인증 정보 포함 허용
                config.setMaxAge(3600L); // preflight 캐시 시간 (초)
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
                    "/api/auth/login", 
                    "/api/auth/register", 
                    "/api/auth/check-email",
                    "/api/auth/send-verification-email",  // 이메일 인증 코드 발송
                    "/api/auth/verify-email",            // 이메일 인증 코드 확인
                    "/css/**", 
                    "/js/**", 
                    "/images/**",        // 정적 리소스
                    "/api/products/all", 
                    "/api/categories/tree", // 상품 정보 조회 API
                    "/api/barcode",
                    "/api/customer/**"  // 모든 고객 관련 API 허용
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
                .requestMatchers("/api/headquarters/hr/approve/**").hasAnyRole("HQ_HRM", "HQ_HRM_M", "MASTER") // Allow both HR staff and managers for approval
                .requestMatchers("/api/headquarters/hr/**").hasAnyRole("HQ_HRM", "HQ_HRM_M", "MASTER") // 인사팀 전체 접근 가능
                .requestMatchers("/api/departments").hasAnyRole("HQ", "HQ_HRM", "HQ_PRO", "HQ_BR", "MASTER", "STORE") // 부서 정보 조회
                .requestMatchers("/api/employees/**").hasAnyRole("HQ", "HQ_HRM", "HQ_PRO", "HQ_BR", "MASTER", "STORE") // 사원 정보 조회
                .requestMatchers("/api/employee-management/**").hasAnyRole("HQ", "HQ_HRM", "HQ_PRO", "HQ_BR", "MASTER", "STORE") // 사원 관리 API
                .requestMatchers("/api/stores/owners").hasAnyRole("HQ", "HQ_HRM", "HQ_PRO", "HQ_BR", "MASTER", "STORE") // 점주 목록 조회 API
                
                // 연차 관련 API - 명시적으로 권한 부여
                .requestMatchers("/api/hr/annual-leave/**").hasAnyRole("HQ", "HQ_HRM", "HQ_HRM_M", "HQ_PRO", "HQ_PRO_M", "HQ_BR", "HQ_BR_M", "MASTER", "STORE")
                .requestMatchers("/api/hr/attendance/**").hasAnyRole("HQ", "HQ_HRM", "HQ_HRM_M", "HQ_PRO", "HQ_PRO_M", "HQ_BR", "HQ_BR_M", "MASTER", "STORE")
                
                // 근태 관리 API - 본사 직원만 접근 가능 (점주는 제외)
                .requestMatchers("/api/hr/**").hasAnyRole("HQ", "HQ_HRM", "HQ_HRM_M", "HQ_PRO", "HQ_PRO_M", "HQ_BR", "HQ_BR_M", "MASTER", "STORE")
                
                // 본사 직원 급여 내역 조회 API
                .requestMatchers("/api/hr/my-salary").hasAnyRole("HQ", "HQ_HRM", "HQ_HRM_M", "HQ_PRO", "HQ_PRO_M", "HQ_BR", "HQ_BR_M", "MASTER")
                
                // 4.3 지점 관리 기능 
                .requestMatchers("/api/store-inquiries/**").hasAnyRole("HQ_BR", "HQ_BR_M", "MASTER")
                // GET 요청은 모든 인증된 사용자에게 허용
                .requestMatchers(HttpMethod.GET, "/api/headquarters/branches/**")
                    .hasAnyRole("HQ", "HQ_HRM", "HQ_HRM_M", "HQ_PRO", "HQ_PRO_M", "HQ_BR", "HQ_BR_M", "MASTER", "STORE")
                
                // POST, PUT, DELETE는 지점관리팀과 마스터만 가능
                .requestMatchers(HttpMethod.POST, "/api/headquarters/branches/**").hasAnyRole("HQ_BR", "HQ_BR_M", "MASTER")
                .requestMatchers(HttpMethod.PUT, "/api/headquarters/branches/**").hasAnyRole("HQ_BR", "HQ_BR_M", "MASTER")
                .requestMatchers(HttpMethod.DELETE, "/api/headquarters/branches/**").hasAnyRole("HQ_BR", "HQ_BR_M", "MASTER")
                
                // 4.4 게시판 기능 - 모든 역할 명시적 허용
                // 게시글 GET 조회는 모든 인증된 사용자(본사 및 점주) 허용
                .requestMatchers(HttpMethod.GET, "/api/headquarters/board/**")
                    .hasAnyRole("HQ", "HQ_HRM", "HQ_HRM_M", "HQ_PRO", "HQ_PRO_M", "HQ_BR", "HQ_BR_M", "MASTER", "STORE", "NON_STORE", "NON_HQ")
                
                // 게시판 최근 게시글 조회 API (위젯용)
                .requestMatchers("/api/headquarters/board/recent")
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
                .requestMatchers("/api/salary/**").hasAnyRole("STORE", "MASTER", "HQ")
                .requestMatchers("/api/parttimer-schedule/**").hasAnyRole("HQ", "OWNER", "STORE")
                .requestMatchers("/api/store/parttimer/**").permitAll()
                .requestMatchers("/api/purchase-orders/**").hasAnyRole("STORE", "MASTER")
                .requestMatchers("/api/shift-schedules/**").hasAnyRole("STORE", "MASTER")
                .requestMatchers("/api/store/**").permitAll()

                // 통합 재고 모니터링 API 보호 - 본사 및 점주 접근 가능
                .requestMatchers("/api/integrated-stock/**").hasAnyRole("HQ", "HQ_HRM", "HQ_PRO", "HQ_BR", "MASTER", "STORE")

                // POS API 보호 - 점주와 마스터만 접근 가능
                .requestMatchers("/api/pos/**").hasAnyRole("STORE", "MASTER")

                // 6. MASTER 권한 모든 API 접근 설정 (전체 경로 설정)
                // ⚠️ 주의: 이 설정을 활성화하면 위의 모든 설정보다 우선 적용되어 MASTER 외 다른 역할은 접근이 제한됩니다.
                // 만약 활성화하려면 이 설정을 맨 마지막(authenticated 위)에 위치시켜야 합니다.
//                .requestMatchers("/**").hasRole("MASTER")
                
                // 7. 그 외 모든 요청은 인증 필요 (기본 설정)
                .anyRequest().authenticated() // 명시되지 않은 모든 URL은 인증된 사용자만 접근 가능
            );
            
        // 디버깅 메시지 추가
        System.out.println("==== 근태 관리 API 설정 완료: 본사 직원만 접근 가능 ====");
            
        // JWT 인증 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        System.out.println("==== SecurityConfig 설정 완료 ====");

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