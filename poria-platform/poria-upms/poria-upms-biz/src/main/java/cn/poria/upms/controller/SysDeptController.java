package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.upms.api.entity.SysDept;
import cn.poria.upms.api.entity.SysDeptRelation;
import cn.poria.upms.service.SysDeptRelationService;
import cn.poria.upms.service.SysDeptService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/dept"})
@Tag(name = "dept", description = "部门管理模块")
public class SysDeptController {

    private final SysDeptService sysDeptService;

    @Resource
    private final SysDeptRelationService relationService;

    @GetMapping({"/tree"})
    @PreAuthorize("@pms.hasPermission('sys_dept_tree')")
    @Operation(summary = "返回树形菜单集合", description = "返回树形菜单集合 (sys_dept_tree)")
    public R getTree() {
        SecurityUtils.getUser();
        return R.ok(this.sysDeptService.selectTree());
    }

    @SysLog("添加部门")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('sys_dept_add')")
    @Operation(summary = "添加部门", description = "添加部门（sys_dept_add）")
    public R save(@RequestBody @Valid SysDept sysDept) {
        return R.ok(this.sysDeptService.saveDept(sysDept));
    }

    @SysLog("删除部门")
    @DeleteMapping({"/{id}"})
    @PreAuthorize("@pms.hasPermission('sys_dept_del')")
    @Operation(summary = "删除部门", description = "删除部门（sys_dept_del）")
    public R removeById(@PathVariable Long id) {
        return R.ok(this.sysDeptService.removeDeptById(id));
    }

    @SysLog("编辑部门")
    @PutMapping
    @Operation(summary = "编辑部门", description = "编辑部门（sys_dept_edit）")
    @PreAuthorize("@pms.hasPermission('sys_dept_edit')")
    public R update(@RequestBody @Valid SysDept sysDept) {
        sysDept.setUpdateTime(LocalDateTime.now());
        return R.ok(this.sysDeptService.updateDeptById(sysDept));
    }

    @GetMapping({"/getDescendantList/{deptId}"})
    @Operation(summary = "查询下级部门关系", description = "查询下级部门关系(sys_dept_descendants)")
    @PreAuthorize("@pms.hasPermission('sys_dept_descendants')")
    public R getDescendantList(@PathVariable Long deptId) {
        return R.ok(this.relationService.list(Wrappers.<SysDeptRelation>lambdaQuery().eq(SysDeptRelation::getAncestor, deptId)));
    }

    @PostMapping({"/batch/getDescendantList"})
    @Operation(summary = "批量查询下级部门关系", description = "批量查询下级部门关系(sys_dept_descendants_batch)")
    @PreAuthorize("@pms.hasPermission('sys_dept_descendants_batch')")
    public R<List<SysDeptRelation>> getDescendantListBatch(@RequestParam("deptIds") List<Long> deptIds) {
        return R.ok(this.relationService.list(Wrappers.<SysDeptRelation>lambdaQuery().in(SysDeptRelation::getAncestor, deptIds)));
    }
}
