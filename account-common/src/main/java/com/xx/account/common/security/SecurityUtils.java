package com.xx.account.common.security;

import com.xx.account.common.core.BusinessException;
import com.xx.account.common.core.ResultCode;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 */
public final class SecurityUtils {

    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取当前登录用户
     */
    public static LoginUser getCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof LoginUser) {
                return (LoginUser) principal;
            }
        } catch (Exception e) {
            // 无认证上下文
        }
        return null;
    }

    /**
     * 获取当前用户 ID
     */
    public static Long getCurrentUserId() {
        LoginUser user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        LoginUser user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取当前部门 ID
     */
    public static Long getCurrentDeptId() {
        LoginUser user = getCurrentUser();
        return user != null ? user.getDeptId() : null;
    }

    /**
     * 判断是否为超级管理员
     */
    public static boolean isAdmin() {
        LoginUser user = getCurrentUser();
        return user != null && Boolean.TRUE.equals(user.getIsAdmin());
    }

    /**
     * 判断是否有某个角色
     */
    public static boolean hasRole(String role) {
        LoginUser user = getCurrentUser();
        if (user == null) {
            return false;
        }
        // 超级管理员拥有所有权限
        if (Boolean.TRUE.equals(user.getIsAdmin())) {
            return true;
        }
        return user.getRoles().contains(role);
    }

    /**
     * 判断是否有某个权限
     */
    public static boolean hasPermission(String permission) {
        LoginUser user = getCurrentUser();
        if (user == null) {
            return false;
        }
        // 超级管理员拥有所有权限
        if (Boolean.TRUE.equals(user.getIsAdmin())) {
            return true;
        }
        return user.getPermissions().contains(permission);
    }

    /**
     * 验证是否有某个权限，没有则抛出异常
     */
    public static void checkPermission(String permission) {
        if (!hasPermission(permission)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED);
        }
    }

    /**
     * 验证是否有某个角色，没有则抛出异常
     */
    public static void checkRole(String role) {
        if (!hasRole(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
