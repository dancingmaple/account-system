package com.xx.account.auth.service.impl;

import com.xx.account.auth.dto.LoginRequest;
import com.xx.account.auth.dto.LoginResponse;
import com.xx.account.auth.dto.RefreshTokenRequest;
import com.xx.account.auth.dto.RegisterRequest;
import com.xx.account.auth.service.AuthService;
import com.xx.account.common.core.BusinessException;
import com.xx.account.common.core.Constants;
import com.xx.account.common.core.ResultCode;
import com.xx.account.common.security.JwtService;
import com.xx.account.common.security.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiration:7200}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration:604800}")
    private long refreshTokenExpiration;

    @Override
    public LoginResponse login(LoginRequest request) {
        // TODO: 从数据库获取用户信息
        // 这里需要调用 UserService 获取用户并验证密码
        // LoginUser user = userService.loadUserByUsername(request.getUsername());

        // 演示数据 - 实际应从数据库查询
        LoginUser user = createDemoUser(request.getUsername());

        // 验证密码 (演示用，实际需要从数据库比对)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 增加登录失败计数
            incrementLoginErrorCount(request.getUsername());
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }

        // 生成 Token
        String accessToken = generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        // 缓存用户信息到 Redis
        cacheUserInfo(user, accessToken);

        log.info("用户登录成功: {}", user.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpiration)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getNickname())
                .build();
    }

    @Override
    public void logout(String token) {
        if (token != null && token.startsWith(Constants.BEARER_PREFIX)) {
            token = token.substring(Constants.BEARER_PREFIX.length());

            // 将 Token 加入黑名单
            String blacklistKey = Constants.REDIS_TOKEN_BLACKLIST + token;
            redisTemplate.opsForValue().set(blacklistKey, "1", accessTokenExpiration, TimeUnit.SECONDS);

            log.info("用户登出，Token 加入黑名单");
        }
    }

    @Override
    public LoginResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 验证 Refresh Token
        if (!jwtService.validateToken(refreshToken)) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_EXPIRED);
        }

        String username = jwtService.extractUsername(refreshToken);

        // TODO: 从数据库获取用户信息
        LoginUser user = createDemoUser(username);

        // 生成新 Token
        String newAccessToken = generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        // 更新缓存
        cacheUserInfo(user, newAccessToken);

        log.info("刷新 Token 成功: {}", username);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(accessTokenExpiration)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    @Override
    public void register(RegisterRequest request) {
        // TODO: 实现用户注册逻辑
        // 1. 验证验证码
        // 2. 验证密码和确认密码
        // 3. 检查用户名/邮箱/手机号是否已存在
        // 4. 加密密码并保存用户

        log.info("用户注册: {}", request.getUsername());
    }

    @Override
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    private String generateAccessToken(LoginUser user) {
        return jwtService.generateAccessToken(user.getUsername(), java.util.Map.of(
                Constants.CLAIM_USER_ID, user.getUserId(),
                Constants.CLAIM_DEPT_ID, user.getDeptId() != null ? user.getDeptId() : 0,
                Constants.CLAIM_DATA_SCOPE, user.getDataScope() != null ? user.getDataScope() : 4,
                Constants.CLAIM_IS_ADMIN, user.getIsAdmin() != null && user.getIsAdmin() ? 1 : 0,
                Constants.CLAIM_ROLES, user.getRoles(),
                Constants.CLAIM_PERMISSIONS, user.getPermissions()
        ));
    }

    private void cacheUserInfo(LoginUser user, String token) {
        // 缓存用户信息
        String userKey = Constants.REDIS_USER_INFO + user.getUserId();
        redisTemplate.opsForValue().set(userKey, user, accessTokenExpiration, TimeUnit.SECONDS);

        // 缓存 Token 到用户映射
        String tokenKey = Constants.REDIS_PREFIX + "token:user:" + token;
        redisTemplate.opsForValue().set(tokenKey, user.getUserId(), accessTokenExpiration, TimeUnit.SECONDS);
    }

    private void incrementLoginErrorCount(String username) {
        String key = Constants.REDIS_LOGIN_ERROR + username;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }
    }

    // 演示用户 - 实际应从数据库查询
    private LoginUser createDemoUser(String username) {
        return new LoginUser(
                1L,
                username,
                passwordEncoder.encode("123456"),
                "admin@example.com",
                "13800138000",
                "管理员",
                null,
                1L,
                Constants.DATA_SCOPE_ALL,
                java.util.List.of(),
                true,
                java.util.List.of("admin", "user"),
                java.util.List.of("*:*:*")
        );
    }
}