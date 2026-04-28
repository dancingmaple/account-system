package com.xx.account.common.security.annotation;

/**
 * 脱敏类型
 */
public enum MaskType {
    /**
     * 手机号：138****1234
     */
    PHONE,
    /**
     * 邮箱：tes****@example.com
     */
    EMAIL,
    /**
     * 身份证：110101********1234
     */
    ID_CARD,
    /**
     * 银行卡：6222 **** **** 1234
     */
    BANK_CARD,
    /**
     * 姓名：张*三 (仅保留首尾)
     */
    NAME,
    /**
     * 地址：北京市朝阳区****
     */
    ADDRESS,
    /**
     * 密码：始终显示 ***
     */
    PASSWORD
}
