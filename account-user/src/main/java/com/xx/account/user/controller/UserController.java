package com.xx.account.user.controller;

import com.xx.account.common.core.R;
import com.xx.account.common.security.SecurityUtils;
import com.xx.account.user.dto.UserDto;
import com.xx.account.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户 CRUD 操作")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "获取用户列表")
    @PreAuthorize("hasAuthority('user:list') or hasRole('admin')")
    public R<List<UserDto>> list(UserDto query) {
        return R.ok(userService.listUsers(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    @PreAuthorize("hasAuthority('user:info') or hasRole('admin')")
    public R<UserDto> get(@PathVariable("id") Long id) {
        return R.ok(userService.getUserInfo(id));
    }

    @PostMapping
    @Operation(summary = "创建用户")
    @PreAuthorize("hasAuthority('user:add') or hasRole('admin')")
    public R<Long> create(@Valid @RequestBody UserDto userDto) {
        Long userId = userService.createUser(userDto);
        return R.ok(userId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    @PreAuthorize("hasAuthority('user:edit') or hasRole('admin')")
    public R<Void> update(@PathVariable("id") Long id, @Valid @RequestBody UserDto userDto) {
        userDto.setId(id);
        userService.updateUser(userDto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    @PreAuthorize("hasAuthority('user:delete') or hasRole('admin')")
    public R<Void> delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return R.ok();
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "分配角色")
    @PreAuthorize("hasRole('admin')")
    public R<Void> assignRoles(@PathVariable("id") Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return R.ok();
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "重置密码")
    @PreAuthorize("hasRole('admin')")
    public R<Void> resetPassword(@PathVariable("id") Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return R.ok();
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码")
    public R<Void> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.changePassword(userId, oldPassword, newPassword);
        return R.ok();
    }
}