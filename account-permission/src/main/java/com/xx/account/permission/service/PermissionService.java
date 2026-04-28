package com.xx.account.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xx.account.permission.domain.Role;
import com.xx.account.permission.domain.Menu;
import com.xx.account.permission.dto.RoleDto;
import com.xx.account.permission.dto.MenuDto;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService {

    // ========== 角色管理 ==========

    /**
     * 获取角色列表
     */
    List<RoleDto> listRoles(RoleDto query);

    /**
     * 获取角色详情
     */
    RoleDto getRole(Long id);

    /**
     * 创建角色
     */
    Long createRole(RoleDto roleDto);

    /**
     * 更新角色
     */
    void updateRole(RoleDto roleDto);

    /**
     * 删除角色
     */
    void deleteRole(Long id);

    /**
     * 分配菜单权限
     */
    void assignMenus(Long roleId, List<Long> menuIds);

    /**
     * 分配数据范围
     */
    void assignDataScope(Long roleId, Integer dataScope, List<Long> deptIds);

    // ========== 菜单管理 ==========

    /**
     * 获取菜单树
     */
    List<MenuDto> getMenuTree();

    /**
     * 获取用户菜单
     */
    List<MenuDto> getUserMenus(Long userId);

    /**
     * 获取菜单详情
     */
    MenuDto getMenu(Long id);

    /**
     * 创建菜单
     */
    Long createMenu(MenuDto menuDto);

    /**
     * 更新菜单
     */
    void updateMenu(MenuDto menuDto);

    /**
     * 删除菜单
     */
    void deleteMenu(Long id);

    // ========== 权限检查 ==========

    /**
     * 检查用户是否有权限
     */
    boolean hasPermission(String permission);

    /**
     * 获取用户角色
     */
    List<String> getUserRoles(Long userId);

    /**
     * 获取用户权限
     */
    List<String> getUserPermissions(Long userId);
}