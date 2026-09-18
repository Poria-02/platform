package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.upms.api.entity.SysTag;
import cn.poria.upms.service.SysTagService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "标签分类管理")
@RequestMapping({"/tag"})
public class SysTagController {
   @Resource
   private SysTagService sysTagService;

   @Operation(summary = "分页查询", description = "分页查询(admin_tag_page)")
   @GetMapping({"/page"})
   // @PreAuthorize("@pms.hasPermission('admin_tag_page')")
   public R selectAll(Page<SysTag> page, SysTag sysTag) {
      return R.ok((Page)this.sysTagService.page(page, new QueryWrapper(sysTag)));
   }

   @Operation(summary = "列表查询", description = "列表查询(admin_tag_list)")
   @GetMapping({"/list"})
   // @PreAuthorize("@pms.hasPermission('admin_tag_list')")
   public R<List<SysTag>> list() {
      return R.ok(this.sysTagService.list());
   }

   @Operation(summary = "通过id查询", description = "通过id查询(admin_tag_get)")
   @GetMapping({"/{id}"})
   // @PreAuthorize("@pms.hasPermission('admin_tag_get')")
   public R<SysTag> selectOne(@PathVariable String id) {
      SecurityUtils.getUser();
      return R.ok((SysTag)this.sysTagService.getById(id));
   }

   @PostMapping
   @SysLog("新增标签分类")
   @Operation(summary = "新增标签分类", description = "新增标签分类，权限标识 admin_tag_add")
   // @PreAuthorize("@pms.hasPermission('admin_tag_add')")
   public R<SysTag> insert(@RequestBody @Validated SysTag sysTag) {
      return R.ok(this.sysTagService.saveTag(sysTag));
   }

   @PutMapping
   @SysLog("修改标签分类")
   @Operation(summary = "修改标签分类", description = "修改标签分类，权限标识 admin_tag_edit")
   @CacheEvict(value = {"tag_items"}, allEntries = true)
   // @PreAuthorize("@pms.hasPermission('admin_tag_edit')")
   public R update(@RequestBody @Validated SysTag sysTag) {
      this.sysTagService.updateById(sysTag);
      return R.ok();
   }

   @DeleteMapping({"{id}"})
   @SysLog("通过id删除标签分类")
   @CacheEvict(value = {"tag_items"}, allEntries = true)
   @Operation(summary = "通过ID删除标签分类", description = "通过ID删除标签分类,权限标识admin_tag_del ")
   // @PreAuthorize("@pms.hasPermission('admin_tag_del')")
   public R delete(@PathVariable String id) {
      this.sysTagService.removeById(id);
      return R.ok();
   }
}
