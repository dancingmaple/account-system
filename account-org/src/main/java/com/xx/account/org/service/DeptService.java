package com.xx.account.org.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xx.account.org.domain.Dept;
import com.xx.account.org.dto.DeptDto;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DeptService extends IService<Dept> {

    /**
     * 获取部门树
     */
    List<DeptDto> getDeptTree();

    /**
     * 根据 ID 获取部门
     */
    DeptDto getById(Long id);

    /**
     * 创建部门
     */
    Long createDept(DeptDto deptDto);

    /**
     * 更新部门
     */
    void updateDept(DeptDto deptDto);

    /**
     * 删除部门
     */
    void deleteDept(Long id);

    /**
     * 获取子部门 IDs
     */
    List<Long> getChildDeptIds(Long parentId);
}