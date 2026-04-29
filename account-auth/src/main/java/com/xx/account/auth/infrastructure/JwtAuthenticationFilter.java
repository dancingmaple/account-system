package com.xx.account.auth.infrastructure;

import com.xx.account.common.core.Constants;
import com.xx.account.common.security.JwtService;
import com.xx.account.common.security.LoginUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器（无 Redis 依赖版本）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            try {
                // 解析 Token
                Claims claims = jwtService.parseToken(token);
                String username = claims.getSubject();

                // 从 Token 中构建用户信息
                LoginUser user = createUserFromClaims(claims);

                if (user != null) {
                    // 设置认证信息
                    List<SimpleGrantedAuthority> authorities = user.getPermissions().stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("用户认证成功: {}", username);
                }

            } catch (Exception e) {
                log.warn("Token 验证失败: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith(Constants.BEARER_PREFIX)) {
            return bearer.substring(Constants.BEARER_PREFIX.length());
        }
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
                null,
                null, null, null, null,
                deptId,
                dataScope,
                List.of(),
                isAdmin,
                roles != null ? roles : List.of(),
                permissions != null ? permissions : List.of("*:*:*")
        );
    }
}