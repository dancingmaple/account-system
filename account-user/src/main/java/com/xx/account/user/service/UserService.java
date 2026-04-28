package com.xx.account.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xx.account.user.domain.User;
import com.xx.account.user.dto.UserDto;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 根据用户 ID 查询用户信息 (含角色权限)
     */
    UserDto getUserInfo(Long userId);

    /**
     * 查询用户列表
     */
    List<UserDto> listUsers(UserDto query);

    /**
     * 创建用户
     */
    Long createUser(UserDto userDto);

    /**
     * 更新用户
     */
    void updateUser(UserDto userDto);

    /**
     * 删除用户
     */
    void deleteUser(Long userId);

    /**
     * 分配角色
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 重置密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}