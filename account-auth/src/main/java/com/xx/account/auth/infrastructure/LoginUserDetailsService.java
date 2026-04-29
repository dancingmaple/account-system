package com.xx.account.auth.infrastructure;

import com.xx.account.common.core.Constants;
import com.xx.account.common.security.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 用户详情服务实现
 * TODO: 实际实现应从数据库查询用户信息
 */
@Slf4j
@Component
public class LoginUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 从数据库查询用户
        // User user = userMapper.selectByUsername(username);
        // if (user == null) {
        //     throw new UsernameNotFoundException("用户不存在: " + username);
        // }

        // 演示数据
        log.debug("加载用户详情: {}", username);

        return new LoginUser(
                1L,
                username,
                "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5lWkJ8RfL.7F2", // 123456
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