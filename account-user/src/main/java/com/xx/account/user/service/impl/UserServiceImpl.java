package com.xx.account.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.account.common.core.BusinessException;
import com.xx.account.common.core.Constants;
import com.xx.account.common.core.ResultCode;
import com.xx.account.user.domain.User;
import com.xx.account.user.domain.UserMapper;
import com.xx.account.user.dto.UserDto;
import com.xx.account.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, Constants.NOT_DELETED));
    }

    @Override
    public UserDto getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        return convertToDto(user);
    }

    @Override
    public List<UserDto> listUsers(UserDto query) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, Constants.NOT_DELETED);

        if (StringUtils.hasText(query.getUsername())) {
            wrapper.like(User::getUsername, query.getUsername());
        }
        if (StringUtils.hasText(query.getNickname())) {
            wrapper.like(User::getNickname, query.getNickname());
        }
        if (StringUtils.hasText(query.getEmail())) {
            wrapper.eq(User::getEmail, query.getEmail());
        }
        if (StringUtils.hasText(query.getPhone())) {
            wrapper.eq(User::getPhone, query.getPhone());
        }
        if (query.getStatus() != null) {
            wrapper.eq(User::getStatus, query.getStatus());
        }
        if (query.getDeptId() != null) {
            wrapper.eq(User::getDeptId, query.getDeptId());
        }
        if (query.getOrgId() != null) {
            wrapper.eq(User::getOrgId, query.getOrgId());
        }

        List<User> users = this.list(wrapper);
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserDto userDto) {
        // 检查用户名是否已存在
        if (getByUsername(userDto.getUsername()) != null) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXISTS);
        }

        // 检查邮箱是否已存在
        if (StringUtils.hasText(userDto.getEmail())) {
            Long count = this.count(new LambdaQueryWrapper<User>()
                    .eq(User::getEmail, userDto.getEmail())
                    .eq(User::getDeleted, Constants.NOT_DELETED));
            if (count > 0) {
                throw new BusinessException(ResultCode.EMAIL_ALREADY_EXISTS);
            }
        }

        // 检查手机号是否已存在
        if (StringUtils.hasText(userDto.getPhone())) {
            Long count = this.count(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, userDto.getPhone())
                    .eq(User::getDeleted, Constants.NOT_DELETED));
            if (count > 0) {
                throw new BusinessException(ResultCode.PHONE_ALREADY_EXISTS);
            }
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode("123456")); // 默认密码
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setNickname(userDto.getNickname());
        user.setAvatar(userDto.getAvatar());
        user.setGender(userDto.getGender());
        user.setOrgId(userDto.getOrgId());
        user.setDeptId(userDto.getDeptId());
        user.setStatus(Constants.USER_STATUS_NORMAL);
        user.setIsAdmin(Constants.NOT_ADMIN);

        this.save(user);
        log.info("创建用户成功: {}", user.getUsername());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDto userDto) {
        User user = this.getById(userDto.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 更新字段
        if (StringUtils.hasText(userDto.getNickname())) {
            user.setNickname(userDto.getNickname());
        }
        if (StringUtils.hasText(userDto.getAvatar())) {
            user.setAvatar(userDto.getAvatar());
        }
        if (userDto.getGender() != null) {
            user.setGender(userDto.getGender());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPhone() != null) {
            user.setPhone(userDto.getPhone());
        }
        if (userDto.getDeptId() != null) {
            user.setDeptId(userDto.getDeptId());
        }
        if (userDto.getOrgId() != null) {
            user.setOrgId(userDto.getOrgId());
        }
        if (userDto.getStatus() != null) {
            user.setStatus(userDto.getStatus());
        }

        this.updateById(user);
        log.info("更新用户成功: {}", user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 逻辑删除
        this.removeById(userId);
        log.info("删除用户成功: {}", user.getUsername());
    }

    @Override
    public void assignRoles(Long userId, List<Long> roleIds) {
        // TODO: 实现角色分配逻辑
        log.info("为用户 {} 分配角色: {}", userId, roleIds);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
        log.info("重置用户密码: {}", user.getUsername());
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
        log.info("修改用户密码: {}", user.getUsername());
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setGender(user.getGender());
        dto.setOrgId(user.getOrgId());
        dto.setDeptId(user.getDeptId());
        dto.setStatus(user.getStatus());
        dto.setIsAdmin(Constants.IS_ADMIN.equals(user.getIsAdmin()));
        dto.setLoginIp(user.getLoginIp());
        dto.setLoginDate(user.getLoginDate() != null ? user.getLoginDate().toString() : null);
        return dto;
    }
}