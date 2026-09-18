package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.upms.api.entity.SysSocialDetails;
import cn.poria.upms.service.SysSocialDetailsService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "第三方账号信息")
@RequestMapping("/social/details")
public class SysSocialDetailsController {

   private final SysSocialDetailsService socialDetailsService;

   @GetMapping("/page")
   @Operation(summary = "分页查询第三方账号")
   public R page(Page page, SysSocialDetails details) {
      return R.ok(this.socialDetailsService.page(page, Wrappers.query(details).orderByDesc("create_time")));
   }

   @PostMapping
   @SysLog("新增第三方账号")
   @Operation(summary = "新增第三方账号")
   public R save(@Valid @RequestBody SysSocialDetails details) {
      return R.ok(this.socialDetailsService.save(details));
   }

   @PutMapping
   @SysLog("修改第三方账号")
   @Operation(summary = "修改第三方账号")
   public R update(@Valid @RequestBody SysSocialDetails details) {
      return R.ok(this.socialDetailsService.updateById(details));
   }

   @DeleteMapping("/{id}")
   @SysLog("删除第三方账号")
   @Operation(summary = "删除第三方账号")
   public R remove(@PathVariable Integer id) {
      return R.ok(this.socialDetailsService.removeById(id));
   }

   public SysSocialDetailsController(SysSocialDetailsService socialDetailsService) {
      this.socialDetailsService = socialDetailsService;
   }
}
