package com.xx.account.org.controller;

import com.xx.account.common.core.R;
import com.xx.account.org.dto.DeptDto;
import com.xx.account.org.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/api/v1/depts")
@RequiredArgsConstructor
@Tag(name = "部门管理", description = "部门 CRUD 操作")
public class DeptController {

    private final DeptService deptService;

    @GetMapping
    @Operation(summary = "获取部门树")
    @PreAuthorize("hasAuthority('dept:list') or hasRole('admin')")
    public R<List<DeptDto>> tree() {
        return R.ok(deptService.getDeptTree());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取部门详情")
    @PreAuthorize("hasAuthority('dept:info') or hasRole('admin')")
    public R<DeptDto> get(@PathVariable Long id) {
        return R.ok(deptService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建部门")
    @PreAuthorize("hasAuthority('dept:add') or hasRole('admin')")
    public R<Long> create(@Valid @RequestBody DeptDto deptDto) {
        return R.ok(deptService.createDept(deptDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新部门")
    @PreAuthorize("hasAuthority('dept:edit') or hasRole('admin')")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody DeptDto deptDto) {
        deptDto.setId(id);
        deptService.updateDept(deptDto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    @PreAuthorize("hasAuthority('dept:delete') or hasRole('admin')")
    public R<Void> delete(@PathVariable Long id) {
        deptService.deleteDept(id);
        return R.ok();
    }
}