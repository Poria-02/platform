package cn.poria.upms.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysDept;
import cn.poria.upms.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dept")
@Tag(name = "部门内部接口")
public class SysDeptInnerController {

    private final SysDeptService sysDeptService;

    @Inner(false)
    @GetMapping({"/detail/{id}"})
    public R getById(@PathVariable Long id) {
        return R.ok(this.sysDeptService.getById(id));
    }

    @Inner(false)
    @GetMapping({"/tree/{departId}"})
    public R getTree(@PathVariable("departId") Long departId) {
        return R.ok(this.sysDeptService.selectTree(departId));
    }

    @Inner
    @GetMapping({"/getOrgByDeptId/{deptId}"})
    public R<SysDept> getOrgByDeptId(@PathVariable("deptId") Long deptId) {
        return R.ok(this.sysDeptService.getOrgByDeptId(deptId));
    }

    @Inner
    @GetMapping("/inner/descendants/{deptId}")
    @Operation(summary = "内部查询本部门及所有下级部门ID")
    public R<List<Long>> getDeptIdsWithChildrenInner(@PathVariable("deptId") Long deptId) {
        return R.ok(this.sysDeptService.getDeptIdsWithChildren(deptId));
    }

    @SysLog("添加部门-内部接口")
    @Inner
    @PostMapping({"inner/save"})
    public R saveInner(@RequestBody @Valid SysDept sysDept) {
        return R.ok(this.sysDeptService.saveDept(sysDept));
    }

    @SysLog("删除部门-内部接口")
    @Inner
    @DeleteMapping({"inner/{id}"})
    public R removeByIdInner(@PathVariable Long id) {
        return R.ok(this.sysDeptService.removeDeptById(id));
    }

    @SysLog("编辑部门-内部接口")
    @Inner
    @PutMapping({"inner/update"})
    public R updateInner(@RequestBody @Valid SysDept sysDept) {
        sysDept.setUpdateTime(LocalDateTime.now());
        return R.ok(this.sysDeptService.updateDeptById(sysDept));
    }
}
