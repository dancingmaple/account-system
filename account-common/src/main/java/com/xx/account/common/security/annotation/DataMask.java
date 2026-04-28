package com.xx.account.common.security.annotation;

import java.lang.annotation.*;

/**
 * 数据权限字段脱敏注解
 * 用于 DTO 字段，根据用户权限决定是否脱敏显示
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataMask {

    /**
     * 脱敏类型
     */
    MaskType type() default MaskType.PHONE;

    /**
     * 需要脱敏显示的权限，拥有该权限则不脱敏
     */
    String viewPermission() default "";
}
