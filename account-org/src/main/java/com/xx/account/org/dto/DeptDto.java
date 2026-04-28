package com.xx.account.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 部门 DTO
 */
@Data
@Schema(description = "部门信息")
public class DeptDto {

    @Schema(description = "部门 ID")
    private Long id;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "父部门 ID")
    private Long parentId;

    @Schema(description = "祖级列表")
    private String ancestors;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "负责人")
    private String leader;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态 0-禁用 1-正常")
    private Integer status;

    @Schema(description = "子部门")
    private List<DeptDto> children;
}