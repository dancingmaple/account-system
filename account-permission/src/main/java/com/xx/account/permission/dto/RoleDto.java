package com.xx.account.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 角色 DTO
 */
@Data
@Schema(description = "角色信息")
public class RoleDto {

    @Schema(description = "角色 ID")
    private Long id;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "数据范围 1-全部 2-本部门及子部门 3-本部门 4-本人 5-自定义")
    private Integer dataScope;

    @Schema(description = "自定义部门 IDs")
    private List<Long> customDeptIds;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态 0-禁用 1-正常")
    private Integer status;

    @Schema(description = "菜单权限 IDs")
    private List<Long> menuIds;
}