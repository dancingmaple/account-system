package com.xx.account.permission.controller;

import com.xx.account.common.core.R;
import com.xx.account.common.security.SecurityUtils;
import com.xx.account.permission.dto.MenuDto;
import com.xx.account.permission.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Tag(name = "菜单管理", description = "菜单 CRUD 操作")
public class MenuController {

    private final PermissionService permissionService;

    @GetMapping("/tree")
    @Operation(summary = "获取菜单树")
    @PreAuthorize("hasAuthority('menu:list') or hasRole('admin')")
    public R<List<MenuDto>> tree() {
        return R.ok(permissionService.getMenuTree());
    }

    @GetMapping("/user")
    @Operation(summary = "获取用户菜单", description = "获取当前用户可访问的菜单")
    public R<List<MenuDto>> userMenus() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(permissionService.getUserMenus(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取菜单详情")
    @PreAuthorize("hasAuthority('menu:info') or hasRole('admin')")
    public R<MenuDto> get(@PathVariable Long id) {
        return R.ok(permissionService.getMenu(id));
    }

    @PostMapping
    @Operation(summary = "创建菜单")
    @PreAuthorize("hasAuthority('menu:add') or hasRole('admin')")
    public R<Long> create(@Valid @RequestBody MenuDto menuDto) {
        return R.ok(permissionService.createMenu(menuDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新菜单")
    @PreAuthorize("hasAuthority('menu:edit') or hasRole('admin')")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody MenuDto menuDto) {
        menuDto.setId(id);
        permissionService.updateMenu(menuDto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    @PreAuthorize("hasAuthority('menu:delete') or hasRole('admin')")
    public R<Void> delete(@PathVariable Long id) {
        permissionService.deleteMenu(id);
        return R.ok();
    }
}