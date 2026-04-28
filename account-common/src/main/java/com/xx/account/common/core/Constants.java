package com.xx.account.common.core;

/**
 * 常量定义
 */
public final class Constants {

    private Constants() {
        throw new IllegalStateException("Constant class");
    }

    // ========== Redis Key 前缀 ==========
    public static final String REDIS_PREFIX = "account:";
    public static final String REDIS_TOKEN_BLACKLIST = REDIS_PREFIX + "token:blacklist:";
    public static final String REDIS_USER_INFO = REDIS_PREFIX + "user:info:";
    public static final String REDIS_USER_PERMISSIONS = REDIS_PREFIX + "user:permissions:";
    public static final String REDIS_USER_ROLES = REDIS_PREFIX + "user:roles:";
    public static final String REDIS_CAPTCHA = REDIS_PREFIX + "captcha:";
    public static final String REDIS_LOGIN_ERROR = REDIS_PREFIX + "login:error:";
    public static final String REDIS_APP_INFO = REDIS_PREFIX + "app:info:";

    // ========== 用户状态 ==========
    public static final Integer USER_STATUS_DISABLED = 0;
    public static final Integer USER_STATUS_NORMAL = 1;

    // ========== 删除标记 ==========
    public static final Integer DELETED = 1;
    public static final Integer NOT_DELETED = 0;

    // ========== 超级管理员标识 ==========
    public static final String SUPER_ADMIN_ROLE_CODE = "super_admin";
    public static final Integer IS_ADMIN = 1;
    public static final Integer NOT_ADMIN = 0;

    // ========== 数据范围类型 ==========
    /**
     * 全部数据权限
     */
    public static final Integer DATA_SCOPE_ALL = 1;
    /**
     * 本部门及子部门数据权限
     */
    public static final Integer DATA_SCOPE_DEPT_AND_CHILD = 2;
    /**
     * 本部门数据权限
     */
    public static final Integer DATA_SCOPE_DEPT = 3;
    /**
     * 仅本人数据权限
     */
    public static final Integer DATA_SCOPE_SELF = 4;
    /**
     * 自定义数据范围
     */
    public static final Integer DATA_SCOPE_CUSTOM = 5;

    // ========== 菜单类型 ==========
    /**
     * 目录
     */
    public static final Integer MENU_TYPE_DIR = 1;
    /**
     * 菜单
     */
    public static final Integer MENU_TYPE_MENU = 2;
    /**
     * 按钮
     */
    public static final Integer MENU_TYPE_BUTTON = 3;

    // ========== Token Claim Keys ==========
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_DEPT_ID = "deptId";
    public static final String CLAIM_DATA_SCOPE = "dataScope";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_PERMISSIONS = "permissions";
    public static final String CLAIM_IS_ADMIN = "isAdmin";

    // ========== 其他 ==========
    public static final String UTF8 = "UTF-8";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final Long DEFAULT_PAGE_NUM = 1L;
    public static final Long DEFAULT_PAGE_SIZE = 10L;
}
