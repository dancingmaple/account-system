package com.xx.account.common.security.annotation;

import java.lang.annotation.*;

/**
 * 数据范围注解
 * 用于方法级别，自动拼接数据权限过滤条件
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 部门表别名
     */
    String deptAlias() default "";

    /**
     * 用户表别名 (用于仅本人数据权限)
     */
    String userAlias() default "";

    /**
     * 是否忽略超级管理员
     * true: 超级管理员不受数据范围限制
     * false: 超级管理员也受数据范围限制
     */
    boolean ignoreAdmin() default true;
}
