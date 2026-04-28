package com.xx.account.auth.controller;

import com.xx.account.auth.dto.LoginRequest;
import com.xx.account.auth.dto.LoginResponse;
import com.xx.account.auth.dto.RefreshTokenRequest;
import com.xx.account.auth.dto.RegisterRequest;
import com.xx.account.auth.service.AuthService;
import com.xx.account.common.core.R;
import com.xx.account.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、登出、注册、Token刷新")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，返回 Access Token 和 Refresh Token")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return R.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录，将 Token 加入黑名单")
    public R<Void> logout() {
        String token = SecurityUtils.getCurrentUser() != null ?
                SecurityUtils.getCurrentUsername() : null;
        authService.logout(token);
        return R.ok();
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用 Refresh Token 刷新 Access Token")
    public R<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refresh(request);
        return R.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户")
    public R<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return R.ok();
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取已登录用户的详细信息")
    public R<LoginResponse> getCurrentUser() {
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            return R.error(401, "未登录");
        }

        LoginResponse response = LoginResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getNickname())
                .build();

        return R.ok(response);
    }

    @PostMapping("/validate")
    @Operation(summary = "验证 Token", description = "验证 Token 是否有效")
    public R<Boolean> validateToken(@RequestParam String token) {
        boolean valid = authService.validateToken(token);
        return R.ok(valid);
    }
}