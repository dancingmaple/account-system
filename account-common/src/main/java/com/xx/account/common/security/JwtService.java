package com.xx.account.common.security;

import com.xx.account.common.core.BusinessException;
import com.xx.account.common.core.ResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT Token 服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:7200}")
    private long expiration; // 秒

    @Value("${jwt.refresh-expiration:604800}")
    private long refreshExpiration; // 秒

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成访问 Token
     */
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, expiration);
    }

    /**
     * 生成 Refresh Token
     */
    public String generateRefreshToken(String subject) {
        return buildToken(subject, null, refreshExpiration);
    }

    private String buildToken(String subject, Map<String, Object> claims, long expirationSeconds) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .id(java.util.UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        } catch (SignatureException | MalformedJwtException e) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (BusinessException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 Token 中提取用户名
     */
    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从 Token 中提取用户 ID
     */
    public Long extractUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 获取 Token 过期时间
     */
    public Date getExpirationDate(String token) {
        return parseToken(token).getExpiration();
    }

    /**
     * 判断 Token 是否已过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (BusinessException e) {
            return true;
        }
    }

    /**
     * 获取访问 Token 有效期剩余秒数
     */
    public long getRemainingSeconds(String token) {
        Date expiration = getExpirationDate(token);
        long remaining = (expiration.getTime() - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }
}