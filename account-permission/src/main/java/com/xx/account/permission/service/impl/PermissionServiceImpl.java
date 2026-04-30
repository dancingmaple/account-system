package com.xx.account.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.account.common.core.BusinessException;
import com.xx.account.common.core.Constants;
import com.xx.account.common.core.ResultCode;
import com.xx.account.permission.domain.Role;
import com.xx.account.permission.domain.RoleMapper;
import com.xx.account.permission.domain.Menu;
import com.xx.account.permission.domain.MenuMapper;
import com.xx.account.permission.dto.RoleDto;
import com.xx.account.permission.dto.MenuDto;
import com.xx.account.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;

    // ========== 角色管理 ==========

    @Override
    public List<RoleDto> listRoles(RoleDto query) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getDeleted, Constants.NOT_DELETED);

        if (StringUtils.hasText(query.getRoleCode())) {
            wrapper.like(Role::getRoleCode, query.getRoleCode());
        }
        if (StringUtils.hasText(query.getRoleName())) {
            wrapper.like(Role::getRoleName, query.getRoleName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Role::getStatus, query.getStatus());
        }

        wrapper.orderByAsc(Role::getSort);
        List<Role> roles = roleMapper.selectList(wrapper);
        return roles.stream().map(this::convertRoleToDto).collect(Collectors.toList());
    }

    @Override
    public RoleDto getRole(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }
        return convertRoleToDto(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleDto roleDto) {
        Role role = new Role();
        role.setRoleCode(roleDto.getRoleCode());
        role.setRoleName(roleDto.getRoleName());
        role.setDataScope(roleDto.getDataScope() != null ? roleDto.getDataScope() : Constants.DATA_SCOPE_SELF);
        role.setSort(roleDto.getSort() != null ? roleDto.getSort() : 0);
        role.setStatus(Constants.USER_STATUS_NORMAL);

        roleMapper.insert(role);
        log.info("创建角色成功: {}", role.getRoleCode());
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleDto roleDto) {
        Role role = roleMapper.selectById(roleDto.getId());
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }

        role.setRoleName(roleDto.getRoleName());
        role.setDataScope(roleDto.getDataScope());
        role.setSort(roleDto.getSort());
        if (roleDto.getStatus() != null) {
            role.setStatus(roleDto.getStatus());
        }

        roleMapper.updateById(role);
        log.info("更新角色成功: {}", role.getRoleCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }
        roleMapper.deleteById(id);
        log.info("删除角色成功: {}", role.getRoleCode());
    }

    @Override
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // TODO: 实现分配菜单逻辑
        log.info("为角色 {} 分配菜单: {}", roleId, menuIds);
    }

    @Override
    public void assignDataScope(Long roleId, Integer dataScope, List<Long> deptIds) {
        // TODO: 实现分配数据范围逻辑
        log.info("为角色 {} 分配数据范围: {}", roleId, dataScope);
    }

    // ========== 菜单管理 ==========

    @Override
    public List<MenuDto> getMenuTree() {
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getDeleted, Constants.NOT_DELETED)
                .orderByAsc(Menu::getSort));
        return buildMenuTree(menus, 0L);
    }

    @Override
    public List<MenuDto> getUserMenus(Long userId) {
        List<Menu> menus = menuMapper.selectByUserId(userId);
        return buildMenuTree(menus, 0L);
    }

    @Override
    public MenuDto getMenu(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.MENU_NOT_FOUND);
        }
        return convertMenuToDto(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMenu(MenuDto menuDto) {
        Menu menu = new Menu();
        menu.setMenuCode(menuDto.getMenuCode());
        menu.setMenuName(menuDto.getMenuName());
        menu.setParentId(menuDto.getParentId() != null ? menuDto.getParentId() : 0L);
        menu.setPath(menuDto.getPath());
        menu.setComponent(menuDto.getComponent());
        menu.setType(menuDto.getType() != null ? menuDto.getType() : Constants.MENU_TYPE_MENU);
        menu.setPermission(menuDto.getPermission());
        menu.setIcon(menuDto.getIcon());
        menu.setSort(menuDto.getSort() != null ? menuDto.getSort() : 0);
        menu.setVisible(menuDto.getVisible() != null && menuDto.getVisible() ? 1 : 0);
        menu.setStatus(Constants.USER_STATUS_NORMAL);

        menuMapper.insert(menu);
        log.info("创建菜单成功: {}", menu.getMenuCode());
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(MenuDto menuDto) {
        Menu menu = menuMapper.selectById(menuDto.getId());
        if (menu == null) {
            throw new BusinessException(ResultCode.MENU_NOT_FOUND);
        }

        menu.setMenuName(menuDto.getMenuName());
        menu.setParentId(menuDto.getParentId());
        menu.setPath(menuDto.getPath());
        menu.setComponent(menuDto.getComponent());
        menu.setType(menuDto.getType());
        menu.setPermission(menuDto.getPermission());
        menu.setIcon(menuDto.getIcon());
        menu.setSort(menuDto.getSort());
        if (menuDto.getVisible() != null) {
            menu.setVisible(menuDto.getVisible() ? 1 : 0);
        }
        if (menuDto.getStatus() != null) {
            menu.setStatus(menuDto.getStatus());
        }

        menuMapper.updateById(menu);
        log.info("更新菜单成功: {}", menu.getMenuCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.MENU_NOT_FOUND);
        }
        menuMapper.deleteById(id);
        log.info("删除菜单成功: {}", menu.getMenuCode());
    }

    // ========== 权限检查 ==========

    @Override
    public boolean hasPermission(String permission) {
        // TODO: 从当前用户上下文获取权限
        return true;
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        List<Role> roles = roleMapper.selectByUserId(userId);
        return roles.stream().map(Role::getRoleCode).collect(Collectors.toList());
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        List<Menu> menus = menuMapper.selectByUserId(userId);
        return menus.stream()
                .filter(m -> StringUtils.hasText(m.getPermission()))
                .map(Menu::getPermission)
                .collect(Collectors.toList());
    }

    private List<MenuDto> buildMenuTree(List<Menu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    MenuDto dto = convertMenuToDto(menu);
                    dto.setChildren(buildMenuTree(menus, menu.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private RoleDto convertRoleToDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setRoleCode(role.getRoleCode());
        dto.setRoleName(role.getRoleName());
        dto.setDataScope(role.getDataScope());
        dto.setSort(role.getSort());
        dto.setStatus(role.getStatus());
        if (StringUtils.hasText(role.getCustomDeptIds())) {
            dto.setCustomDeptIds(Arrays.stream(role.getCustomDeptIds().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private MenuDto convertMenuToDto(Menu menu) {
        MenuDto dto = new MenuDto();
        dto.setId(menu.getId());
        dto.setMenuCode(menu.getMenuCode());
        dto.setMenuName(menu.getMenuName());
        dto.setParentId(menu.getParentId());
        dto.setPath(menu.getPath());
        dto.setComponent(menu.getComponent());
        dto.setType(menu.getType());
        dto.setPermission(menu.getPermission());
        dto.setIcon(menu.getIcon());
        dto.setSort(menu.getSort());
        dto.setVisible(menu.getVisible() == 1);
        dto.setStatus(menu.getStatus());
        return dto;
    }
}