package com.core.erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("*")); //모든 ip 허용.허용할 FrontEnd ip설정
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // 허용할 HTTP 메서드
                    config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
                    config.setAllowCredentials(true); // 쿠키 및 인증 정보 허용
                    return config;
                }))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login", "/auth/register", "/auth/**", "/css/**", "/js/**", "/images/**",
                                "/headquarters/**", "/store/**", "/api/products/all","api/products/detail/**", "/api/storeStock/**", "/api/storeStock/all", "/api/storeStock/detail/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login")
                        .invalidateHttpSession(true)
                        .permitAll()
                );

        return http.build();
    }
}
