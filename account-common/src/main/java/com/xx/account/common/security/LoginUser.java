package com.xx.account.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录用户详情实现
 */
@Getter
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Long userId;
    private final String username;
    private final String password;
    private final String email;
    private final String phone;
    private final String nickname;
    private final String avatar;
    private final Long deptId;
    private final Integer dataScope;
    private final List<Long> customDeptIds;
    private final Boolean isAdmin;
    private final List<String> roles;
    private final List<String> permissions;
    private final Collection<? extends GrantedAuthority> authorities;

    public LoginUser(Long userId, String username, String password, String email, String phone,
            String nickname, String avatar, Long deptId, Integer dataScope, List<Long> customDeptIds,
            Boolean isAdmin, List<String> roles, List<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
        this.avatar = avatar;
        this.deptId = deptId;
        this.dataScope = dataScope;
        this.customDeptIds = customDeptIds;
        this.isAdmin = isAdmin;
        this.roles = roles;
        this.permissions = permissions;
        this.authorities = buildAuthorities(roles, permissions);
    }

    private Collection<? extends GrantedAuthority> buildAuthorities(List<String> roles, List<String> permissions) {
        List<SimpleGrantedAuthority> auths = new java.util.ArrayList<>();

        // 添加角色权限
        for (String role : roles) {
            auths.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        // 添加操作权限
        for (String permission : permissions) {
            auths.add(new SimpleGrantedAuthority(permission));
        }

        return Collections.unmodifiableList(auths);
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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}