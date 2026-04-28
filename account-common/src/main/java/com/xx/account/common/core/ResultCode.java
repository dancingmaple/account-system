package com.xx.account.common.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应码定义
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),

    // ========== 认证相关 1000-1999 ==========
    UNAUTHORIZED(1001, "未登录或 Token 已过期"),
    TOKEN_INVALID(1002, "Token 无效"),
    TOKEN_EXPIRED(1003, "Token 已过期"),
    LOGIN_FAILED(1004, "用户名或密码错误"),
    ACCOUNT_LOCKED(1005, "账号已被锁定"),
    ACCOUNT_DISABLED(1006, "账号已被禁用"),
    VERIFY_CODE_ERROR(1007, "验证码错误"),
    VERIFY_CODE_EXPIRED(1008, "验证码已过期"),
    LOGOUT_FAILED(1009, "登出失败"),
    REFRESH_TOKEN_EXPIRED(1010, "Refresh Token 已过期"),

    // ========== 权限相关 2000-2999 ==========
    FORBIDDEN(2001, "无权访问"),
    PERMISSION_DENIED(2002, "权限不足"),
    DATA_SCOPE_DENIED(2003, "数据范围外"),
    ROLE_NOT_FOUND(2004, "角色不存在"),
    MENU_NOT_FOUND(2005, "菜单不存在"),

    // ========== 用户相关 3000-3999 ==========
    USER_NOT_FOUND(3001, "用户不存在"),
    USER_ALREADY_EXISTS(3002, "用户已存在"),
    USERNAME_ALREADY_EXISTS(3003, "用户名已存在"),
    EMAIL_ALREADY_EXISTS(3004, "邮箱已存在"),
    PHONE_ALREADY_EXISTS(3005, "手机号已存在"),
    PASSWORD_ERROR(3006, "原密码错误"),
    PASSWORD_TOO_WEAK(3007, "密码强度不足"),
    PASSWORD_IN_HISTORY(3008, "密码不能与历史密码重复"),

    // ========== 组织相关 4000-4999 ==========
    DEPT_NOT_FOUND(4001, "部门不存在"),
    DEPT_NAME_ALREADY_EXISTS(4002, "部门名称已存在"),
    DEPT_HAS_CHILD(4003, "部门存在子部门，无法删除"),
    DEPT_HAS_USER(4004, "部门存在用户，无法删除"),
    PARENT_DEPT_NOT_FOUND(4005, "父部门不存在"),
    PARENT_DEPT_SELF(4006, "父部门不能是自己"),

    // ========== 开放平台相关 5000-5999 ==========
    APP_NOT_FOUND(5001, "应用不存在"),
    APP_KEY_ALREADY_EXISTS(5002, "AppKey 已存在"),
    APP_SECRET_INVALID(5003, "AppSecret 无效"),
    APP_DISABLED(5004, "应用已禁用"),
    APP_EXPIRED(5005, "应用已过期"),
    RATE_LIMIT_EXCEEDED(5006, "请求频率超限"),
    SIGNATURE_INVALID(5007, "签名无效"),
    IP_NOT_ALLOWED(5008, "IP 不在白名单"),

    // ========== 系统相关 9000-9999 ==========
    SYSTEM_ERROR(9001, "系统异常"),
    PARAM_VALIDATION_ERROR(9002, "参数验证失败"),
    DATA_NOT_FOUND(9003, "数据不存在"),
    DATA_ALREADY_DELETED(9004, "数据已删除"),
    OPERATION_FAILED(9005, "操作失败");

    private final Integer code;
    private final String message;
}
