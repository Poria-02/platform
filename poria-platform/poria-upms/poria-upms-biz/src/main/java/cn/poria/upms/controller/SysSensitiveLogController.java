package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.dto.SensitiveInfo;
import cn.poria.upms.api.entity.SysSensitiveLog;
import cn.poria.upms.service.SysSensitiveLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "敏感信息查询日志")
@RequestMapping({"/sensitive/log"})
public class SysSensitiveLogController {
   @Resource
   private SysSensitiveLogService sysSensitiveLogService;

   @Operation(summary = "分页查询", description = "分页查询(sys_sensitive_page)")
   @GetMapping({"/page"})
   // @PreAuthorize("@pms.hasPermission('sys_sensitive_page')")
   public R selectAll(Page<SysSensitiveLog> page, SysSensitiveLog sysSensitiveLog) {
      return R.ok((Page)this.sysSensitiveLogService.page(page, new QueryWrapper(sysSensitiveLog)));
   }

   @Operation(summary = "通过id查询", description = "通过id查询(sys_sensitive_get)")
   @GetMapping({"/{id}"})
   // @PreAuthorize("@pms.hasPermission('sys_sensitive_get')")
   public R selectOne(@PathVariable String id) {
      return R.ok((SysSensitiveLog)this.sysSensitiveLogService.getById(id));
   }

   @PostMapping
   @Hidden
   @Operation(summary = "新增敏感信息查询日志", description = "新增敏感信息查询日志(sys_sensitive_insert)")
   public R saveSensitiveLog(@RequestBody @Validated SensitiveInfo sensitiveInfo) {
      this.sysSensitiveLogService.saveSensitiveLog(sensitiveInfo);
      return R.ok();
   }
}
