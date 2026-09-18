package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.upms.api.entity.SysDept;
import cn.poria.upms.api.entity.SysDeptRelation;
import cn.poria.upms.service.SysDeptRelationService;
import cn.poria.upms.service.SysDeptService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.lang.invoke.SerializedLambda;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/dept"})
@Tag(name = "dept", description = "部门管理模块")
public class SysDeptController {
   private final SysDeptService sysDeptService;
   private final SysDeptRelationService relationService;

   @Inner(false)
   @GetMapping({"/detail/{id}"})
   public R getById(@PathVariable Long id) {
      return R.ok((SysDept)this.sysDeptService.getById(id));
   }

   @GetMapping({"/tree"})
   // @PreAuthorize("@pms.hasPermission('sys_dept_tree')")
   @Operation(summary = "返回树形菜单集合", description = "返回树形菜单集合 (sys_dept_tree)")
   public R getTree() {
      SecurityUtils.getUser();
      return R.ok(this.sysDeptService.selectTree());
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

   @SysLog("添加部门")
   @PostMapping
   // @PreAuthorize("@pms.hasPermission('sys_dept_add')")
   @Operation(summary = "添加部门", description = "添加部门（sys_dept_add）")
   public R save(@RequestBody @Valid SysDept sysDept) {
      return R.ok(this.sysDeptService.saveDept(sysDept));
   }

   @SysLog("添加部门-内部接口")
   @Inner
   @PostMapping({"inner/save"})
   public R saveInner(@RequestBody @Valid SysDept sysDept) {
      return R.ok(this.sysDeptService.saveDept(sysDept));
   }

   @SysLog("删除部门")
   @DeleteMapping({"/{id}"})
   // @PreAuthorize("@pms.hasPermission('sys_dept_del')")
   @Operation(summary = "删除部门", description = "删除部门（sys_dept_del）")
   public R removeById(@PathVariable Long id) {
      return R.ok(this.sysDeptService.removeDeptById(id));
   }

   @SysLog("删除部门-内部接口")
   @Inner
   @DeleteMapping({"inner/{id}"})
   public R removeByIdInner(@PathVariable Long id) {
      return R.ok(this.sysDeptService.removeDeptById(id));
   }

   @SysLog("编辑部门")
   @PutMapping
   @Operation(summary = "编辑部门", description = "编辑部门（sys_dept_edit）")
   // @PreAuthorize("@pms.hasPermission('sys_dept_edit')")
   public R update(@RequestBody @Valid SysDept sysDept) {
      sysDept.setUpdateTime(LocalDateTime.now());
      return R.ok(this.sysDeptService.updateDeptById(sysDept));
   }

   @SysLog("编辑部门-内部接口")
   @Inner
   @PutMapping({"inner/update"})
   public R updateInner(@RequestBody @Valid SysDept sysDept) {
      sysDept.setUpdateTime(LocalDateTime.now());
      return R.ok(this.sysDeptService.updateDeptById(sysDept));
   }

   @GetMapping({"/getDescendantList/{deptId}"})
   @Operation(summary = "查收子级列表", description = "查收子级列表(sys_client_get)")
   public R getDescendantList(@PathVariable Long deptId) {
      return R.ok(this.relationService.list((Wrapper)Wrappers.<SysDeptRelation>lambdaQuery().eq(SysDeptRelation::getAncestor, deptId)));
   }

   @PostMapping({"/batch/getDescendantList"})
   @Operation(summary = "批量查询", description = "批量查询(sys_client_batch)")
   R<List<SysDeptRelation>> getDescendantListBatch(@RequestParam("deptIds") List<Long> deptIds) {
      return R.ok(this.relationService.list((Wrapper)Wrappers.<SysDeptRelation>lambdaQuery().in(SysDeptRelation::getAncestor, deptIds)));
   }

   public SysDeptController(final SysDeptService sysDeptService, final SysDeptRelationService relationService) {
      this.sysDeptService = sysDeptService;
      this.relationService = relationService;
   }

}
