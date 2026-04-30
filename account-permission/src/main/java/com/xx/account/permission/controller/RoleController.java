package com.xx.account.permission.controller;

import com.xx.account.common.core.R;
import com.xx.account.permission.dto.RoleDto;
import com.xx.account.permission.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色 CRUD 操作")
public class RoleController {

    private final PermissionService permissionService;

    @GetMapping
    @Operation(summary = "获取角色列表")
    @PreAuthorize("hasAuthority('role:list') or hasRole('admin')")
    public R<List<RoleDto>> list(RoleDto query) {
        return R.ok(permissionService.listRoles(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情")
    @PreAuthorize("hasAuthority('role:info') or hasRole('admin')")
    public R<RoleDto> get(@PathVariable("id") Long id) {
        return R.ok(permissionService.getRole(id));
    }

    @PostMapping
    @Operation(summary = "创建角色")
    @PreAuthorize("hasAuthority('role:add') or hasRole('admin')")
    public R<Long> create(@Valid @RequestBody RoleDto roleDto) {
        return R.ok(permissionService.createRole(roleDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    @PreAuthorize("hasAuthority('role:edit') or hasRole('admin')")
    public R<Void> update(@PathVariable("id") Long id, @Valid @RequestBody RoleDto roleDto) {
        roleDto.setId(id);
        permissionService.updateRole(roleDto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    @PreAuthorize("hasAuthority('role:delete') or hasRole('admin')")
    public R<Void> delete(@PathVariable("id") Long id) {
        permissionService.deleteRole(id);
        return R.ok();
    }

    @PutMapping("/{id}/menus")
    @Operation(summary = "分配菜单权限")
    @PreAuthorize("hasRole('admin')")
    public R<Void> assignMenus(@PathVariable("id") Long id, @RequestBody List<Long> menuIds) {
        permissionService.assignMenus(id, menuIds);
        return R.ok();
    }

    @PutMapping("/{id}/data-scope")
    @Operation(summary = "分配数据范围")
    @PreAuthorize("hasRole('admin')")
    public R<Void> assignDataScope(@PathVariable("id") Long id,
                                    @RequestParam Integer dataScope,
                                    @RequestBody(required = false) List<Long> deptIds) {
        permissionService.assignDataScope(id, dataScope, deptIds);
        return R.ok();
    }
}