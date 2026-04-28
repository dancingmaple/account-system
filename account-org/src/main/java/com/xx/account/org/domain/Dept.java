package com.xx.account.org.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门实体
 */
@Data
@TableName("sys_dept")
public class Dept {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deptName;

    private Long parentId;

    private String ancestors;

    private Integer sort;

    private String leader;

    private String phone;

    private String email;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}