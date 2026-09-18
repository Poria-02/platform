package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.upms.api.feign.RemoteTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/token"})
@Tag(name = "token", description = "令牌管理模块")
public class SysTokenController {
   private final RemoteTokenService remoteTokenService;

   @GetMapping({"/page"})
   @Operation(summary = "分页token信息", description = "分页token信息(sys_token_get)")
   // @PreAuthorize("@pms.hasPermission('sys_token_get')")
   public R getTokenPage(@RequestParam Map<String, Object> params) {
      return this.remoteTokenService.getTokenPage(params, "Y");
   }

   @SysLog("删除用户token")
   @DeleteMapping({"/{token}"})
   // @PreAuthorize("@pms.hasPermission('sys_token_del')")
   @Operation(summary = "删除用户token", description = "删除用户token(sys_token_del)")
   public R removeById(@PathVariable String token) {
      return this.remoteTokenService.removeTokenById(token, "Y");
   }

   public SysTokenController(final RemoteTokenService remoteTokenService) {
      this.remoteTokenService = remoteTokenService;
   }
}
