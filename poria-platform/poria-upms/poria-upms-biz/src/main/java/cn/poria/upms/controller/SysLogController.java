package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysLog;
import cn.poria.upms.api.vo.PreLogVO;
import cn.poria.upms.service.SysLogService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/log"})
@Tag(name = "log", description = "日志管理模块")
public class SysLogController {
   private final SysLogService sysLogService;

   @GetMapping({"/page"})
   // @PreAuthorize("@pms.hasPermission('sys_log_get')")
   @Operation(summary = "分页查询", description = "分页查询 (sys_log_get)")
   public R getLogPage(Page page, SysLog sysLog) {
      return R.ok((Page)this.sysLogService.page(page, Wrappers.query(sysLog)));
   }

   @DeleteMapping({"/{id}"})
   // @PreAuthorize("@pms.hasPermission('sys_log_del')")
   @Operation(summary = "删除日志", description = "删除日志 (sys_log_del)")
   public R removeById(@PathVariable Long id) {
      return R.ok(this.sysLogService.removeById(id));
   }

   @Inner
   @PostMapping({"/save"})
   public R save(@RequestBody @Valid SysLog sysLog) {
      return R.ok(this.sysLogService.save(sysLog));
   }

   @PostMapping({"/logs"})
   // @PreAuthorize("@pms.hasPermission('sys_log_insert')")
   @Operation(summary = "批量插入前端异常日志", description = "批量插入前端异常日志 (sys_log_insert)")
   public R saveBatchLogs(@RequestBody List<PreLogVO> preLogVoList) {
      return R.ok(this.sysLogService.saveBatchLogs(preLogVoList));
   }

   public SysLogController(final SysLogService sysLogService) {
      this.sysLogService = sysLogService;
   }
}
