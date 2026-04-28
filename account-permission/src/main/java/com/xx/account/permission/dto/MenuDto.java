package com.xx.account.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 菜单 DTO
 */
@Data
@Schema(description = "菜单信息")
public class MenuDto {

    @Schema(description = "菜单 ID")
    private Long id;

    @Schema(description = "菜单编码")
    private String menuCode;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "父菜单 ID")
    private Long parentId;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "类型 1-目录 2-菜单 3-按钮")
    private Integer type;

    @Schema(description = "权限标识")
    private String permission;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否可见")
    private Boolean visible;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "子菜单")
    private List<MenuDto> children;
}