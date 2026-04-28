package com.xx.account.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.account.common.core.BusinessException;
import com.xx.account.common.core.Constants;
import com.xx.account.common.core.ResultCode;
import com.xx.account.org.domain.Dept;
import com.xx.account.org.domain.DeptMapper;
import com.xx.account.org.dto.DeptDto;
import com.xx.account.org.service.DeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Override
    public List<DeptDto> getDeptTree() {
        List<Dept> depts = this.list(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getDeleted, Constants.NOT_DELETED)
                .orderByAsc(Dept::getSort));

        return buildDeptTree(depts, 0L);
    }

    @Override
    public DeptDto getById(Long id) {
        Dept dept = super.getById(id);
        if (dept == null) {
            throw new BusinessException(ResultCode.DEPT_NOT_FOUND);
        }
        return convertToDto(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDept(DeptDto deptDto) {
        // 检查父部门是否存在
        if (deptDto.getParentId() != null && deptDto.getParentId() != 0) {
            Dept parent = super.getById(deptDto.getParentId());
            if (parent == null) {
                throw new BusinessException(ResultCode.PARENT_DEPT_NOT_FOUND);
            }
            deptDto.setAncestors(parent.getAncestors() + "," + deptDto.getParentId());
        } else {
            deptDto.setParentId(0L);
            deptDto.setAncestors("0");
        }

        // 检查部门名称是否已存在
        Long count = this.count(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getDeptName, deptDto.getDeptName())
                .eq(Dept::getParentId, deptDto.getParentId())
                .eq(Dept::getDeleted, Constants.NOT_DELETED));
        if (count > 0) {
            throw new BusinessException(ResultCode.DEPT_NAME_ALREADY_EXISTS);
        }

        Dept dept = new Dept();
        dept.setDeptName(deptDto.getDeptName());
        dept.setParentId(deptDto.getParentId());
        dept.setAncestors(deptDto.getAncestors());
        dept.setSort(deptDto.getSort() != null ? deptDto.getSort() : 0);
        dept.setLeader(deptDto.getLeader());
        dept.setPhone(deptDto.getPhone());
        dept.setEmail(deptDto.getEmail());
        dept.setStatus(Constants.USER_STATUS_NORMAL);

        this.save(dept);
        log.info("创建部门成功: {}", dept.getDeptName());
        return dept.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(DeptDto deptDto) {
        Dept dept = super.getById(deptDto.getId());
        if (dept == null) {
            throw new BusinessException(ResultCode.DEPT_NOT_FOUND);
        }

        // 不能将父部门设为自己
        if (deptDto.getId().equals(deptDto.getParentId())) {
            throw new BusinessException(ResultCode.PARENT_DEPT_SELF);
        }

        // 更新祖先链
        if (deptDto.getParentId() != null && !deptDto.getParentId().equals(dept.getParentId())) {
            Dept parent = super.getById(deptDto.getParentId());
            if (parent == null) {
                throw new BusinessException(ResultCode.PARENT_DEPT_NOT_FOUND);
            }
            String newAncestors = parent.getAncestors() + "," + deptDto.getParentId();

            // 更新所有子部门的祖先
            updateChildrenAncestors(dept.getId(), dept.getAncestors(), newAncestors);

            dept.setAncestors(newAncestors);
        }

        dept.setDeptName(deptDto.getDeptName());
        dept.setParentId(deptDto.getParentId());
        dept.setSort(deptDto.getSort());
        dept.setLeader(deptDto.getLeader());
        dept.setPhone(deptDto.getPhone());
        dept.setEmail(deptDto.getEmail());
        if (deptDto.getStatus() != null) {
            dept.setStatus(deptDto.getStatus());
        }

        this.updateById(dept);
        log.info("更新部门成功: {}", dept.getDeptName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long id) {
        Dept dept = super.getById(id);
        if (dept == null) {
            throw new BusinessException(ResultCode.DEPT_NOT_FOUND);
        }

        // 检查是否有子部门
        Long childCount = this.count(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getParentId, id)
                .eq(Dept::getDeleted, Constants.NOT_DELETED));
        if (childCount > 0) {
            throw new BusinessException(ResultCode.DEPT_HAS_CHILD);
        }

        // 检查是否有用户
        // TODO: 检查部门下是否有用户

        this.removeById(id);
        log.info("删除部门成功: {}", dept.getDeptName());
    }

    @Override
    public List<Long> getChildDeptIds(Long parentId) {
        List<Dept> depts = this.list(new LambdaQueryWrapper<Dept>()
                .likeRight(Dept::getAncestors, parentId + ",")
                .or()
                .eq(Dept::getId, parentId)
                .eq(Dept::getDeleted, Constants.NOT_DELETED));

        return depts.stream().map(Dept::getId).collect(Collectors.toList());
    }

    private List<DeptDto> buildDeptTree(List<Dept> depts, Long parentId) {
        return depts.stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .map(dept -> {
                    DeptDto dto = convertToDto(dept);
                    dto.setChildren(buildDeptTree(depts, dept.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void updateChildrenAncestors(Long deptId, String oldAncestors, String newAncestors) {
        List<Dept> children = this.list(new LambdaQueryWrapper<Dept>()
                .likeRight(Dept::getAncestors, oldAncestors + "," + deptId));

        for (Dept child : children) {
            String updatedAncestors = child.getAncestors().replaceFirst(oldAncestors, newAncestors);
            child.setAncestors(updatedAncestors);
            this.updateById(child);
        }
    }

    private DeptDto convertToDto(Dept dept) {
        DeptDto dto = new DeptDto();
        dto.setId(dept.getId());
        dto.setDeptName(dept.getDeptName());
        dto.setParentId(dept.getParentId());
        dto.setAncestors(dept.getAncestors());
        dto.setSort(dept.getSort());
        dto.setLeader(dept.getLeader());
        dto.setPhone(dept.getPhone());
        dto.setEmail(dept.getEmail());
        dto.setStatus(dept.getStatus());
        return dto;
    }
}