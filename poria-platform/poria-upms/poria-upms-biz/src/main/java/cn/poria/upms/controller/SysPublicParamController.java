package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysPublicParam;
import cn.poria.upms.service.SysPublicParamService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/param"})
@Tag(name = "param", description = "公共参数配置")
public class SysPublicParamController {
   private final SysPublicParamService sysPublicParamService;

   @Inner(false)
   @Operation(summary = "查询公共参数值", description = "根据key查询公共参数值")
   @GetMapping({"/publicValue/{publicKey}"})
   public R publicKey(@PathVariable("publicKey") String publicKey) {
      return R.ok(this.sysPublicParamService.getSysPublicParamKeyToValue(publicKey));
   }

   @Operation(summary = "分页查询", description = "分页查询")
   @GetMapping({"/page"})
   public R getSysPublicParamPage(Page page, SysPublicParam sysPublicParam) {
      return R.ok((Page)this.sysPublicParamService.page(page, Wrappers.query(sysPublicParam)));
   }

   @Operation(summary = "通过id查询公共参数", description = "通过id查询公共参数")
   @GetMapping({"/{publicId}"})
   public R getById(@PathVariable("publicId") Long publicId) {
      return R.ok((SysPublicParam)this.sysPublicParamService.getById(publicId));
   }

   @Operation(summary = "新增公共参数", description = "新增公共参数(admin_syspublicparam_add)")
   @SysLog("新增公共参数")
   @PostMapping
   // @PreAuthorize("@pms.hasPermission('admin_syspublicparam_add')")
   public R save(@RequestBody SysPublicParam sysPublicParam) {
      return R.ok(this.sysPublicParamService.saveParam(sysPublicParam));
   }

   @Operation(summary = "修改公共参数", description = "修改公共参数(admin_syspublicparam_edit)")
   @SysLog("修改公共参数")
   @PutMapping
   // @PreAuthorize("@pms.hasPermission('admin_syspublicparam_edit')")
   public R updateById(@RequestBody SysPublicParam sysPublicParam) {
      return this.sysPublicParamService.updateParam(sysPublicParam);
   }

   @Operation(summary = "删除公共参数", description = "删除公共参数(admin_syspublicparam_del)")
   @SysLog("删除公共参数")
   @DeleteMapping({"/{publicId}"})
   // @PreAuthorize("@pms.hasPermission('admin_syspublicparam_del')")
   public R removeById(@PathVariable Long publicId) {
      return this.sysPublicParamService.removeParam(publicId);
   }

   public SysPublicParamController(final SysPublicParamService sysPublicParamService) {
      this.sysPublicParamService = sysPublicParamService;
   }
}
