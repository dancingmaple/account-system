package com.xx.account.auth.service;

import com.xx.account.auth.dto.LoginRequest;
import com.xx.account.auth.dto.LoginResponse;
import com.xx.account.auth.dto.RefreshTokenRequest;
import com.xx.account.auth.dto.RegisterRequest;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     */
    void logout(String token);

    /**
     * 刷新 Token
     */
    LoginResponse refresh(RefreshTokenRequest request);

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 验证 Token 是否有效
     */
    boolean validateToken(String token);
}