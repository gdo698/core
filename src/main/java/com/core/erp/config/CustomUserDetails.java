package com.core.erp.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Integer storeId;   // 소속 지점 ID
    private final Integer departId;  // 부서 ID (1~10: 본사, 13: 점장)
    private final String username;   // 로그인 ID
    private final String password;   // 비밀번호
    private final Collection<? extends GrantedAuthority> authorities;  // 권한 목록 (Security용)

    public CustomUserDetails(Integer storeId, Integer departId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.storeId = storeId;
        this.departId = departId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정 만료 안 됨
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 계정 잠김 없음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 비밀번호 만료 안 됨
    }

    @Override
    public boolean isEnabled() {
        return true;  // 활성화 계정
    }
}