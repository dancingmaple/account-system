package com.xx.account.auth.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xx.account.common.core.Constants;
import com.xx.account.common.core.R;
import com.xx.account.common.core.ResultCode;
import com.xx.account.common.security.JwtService;
import com.xx.account.common.security.LoginUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JWT 认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            try {
                // 检查 Token 是否在黑名单
                if (isTokenBlacklisted(token)) {
                    sendErrorResponse(response, ResultCode.TOKEN_INVALID);
                    return;
                }

                // 解析 Token
                Claims claims = jwtService.parseToken(token);
                String username = claims.getSubject();

                // 从缓存或数据库获取用户信息
                LoginUser user = getUserFromCache(username);
                if (user == null) {
                    // TODO: 从数据库加载用户
                    user = createUserFromClaims(claims);
                }

                if (user != null) {
                    // 设置认证信息
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("用户认证成功: {}", username);
                }

            } catch (Exception e) {
                log.warn("Token 验证失败: {}", e.getMessage());
                sendErrorResponse(response, ResultCode.TOKEN_INVALID);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith(Constants.BEARER_PREFIX)) {
            return bearer.substring(Constants.BEARER_PREFIX.length());
        }
        return null;
    }

    private boolean isTokenBlacklisted(String token) {
        String blacklistKey = Constants.REDIS_TOKEN_BLACKLIST + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey));
    }

    private LoginUser getUserFromCache(String username) {
        // TODO: 从缓存获取用户信息
        return null;
    }

    private LoginUser createUserFromClaims(Claims claims) {
        Long userId = claims.get(Constants.CLAIM_USER_ID, Long.class);
        Long deptId = claims.get(Constants.CLAIM_DEPT_ID, Long.class);
        Integer dataScope = claims.get(Constants.CLAIM_DATA_SCOPE, Integer.class);
        Boolean isAdmin = claims.get(Constants.CLAIM_IS_ADMIN, Integer.class) == 1;
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get(Constants.CLAIM_ROLES, List.class);
        @SuppressWarnings("unchecked")
        List<String> permissions = claims.get(Constants.CLAIM_PERMISSIONS, List.class);

        return new LoginUser(
                userId,
                claims.getSubject(),
                null, // password 不需要
                null, null, null,
                deptId,
                dataScope,
                List.of(),
                isAdmin,
                roles != null ? roles : List.of(),
                permissions != null ? permissions : List.of()
        );
    }

    private void sendErrorResponse(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        R<Void> result = R.error(resultCode.getCode(), resultCode.getMessage());
        String json = objectMapper.writeValueAsString(result);
        response.getWriter().write(json);
    }
}