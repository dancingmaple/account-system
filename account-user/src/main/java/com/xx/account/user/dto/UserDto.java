package com.xx.account.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户 DTO
 */
@Data
@Schema(description = "用户信息")
public class UserDto {

    @Schema(description = "用户 ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别 0-未知 1-男 2-女")
    private Integer gender;

    @Schema(description = "组织 ID")
    private Long orgId;

    @Schema(description = "部门 ID")
    private Long deptId;

    @Schema(description = "岗位 ID 列表")
    private List<Long> postIds;

    @Schema(description = "状态 0-禁用 1-正常")
    private Integer status;

    @Schema(description = "是否超级管理员")
    private Boolean isAdmin;

    @Schema(description = "角色列表")
    private List<String> roles;

    @Schema(description = "权限列表")
    private List<String> permissions;

    @Schema(description = "最后登录 IP")
    private String loginIp;

    @Schema(description = "最后登录时间")
    private String loginDate;
}