package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysTagItem;
import cn.poria.upms.service.SysTagItemService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@Tag(name = "标签项管理", description = "标签项管理")
@RequestMapping({"/tagItem"})
public class SysTagItemController {
   @Resource
   private SysTagItemService sysTagItemService;

   @Operation(summary = "分页查询", description = "分页查询(admin_tagItem_page)")
   @GetMapping({"/page"})
   // @PreAuthorize("@pms.hasPermission('admin_tagItem_page')")
   public R selectAll(Page<SysTagItem> page, SysTagItem sysTagItem) {
      return R.ok((Page)this.sysTagItemService.page(page, new QueryWrapper(sysTagItem)));
   }

   @Operation(summary = "查询", description = "查询(admin_tagItem_key)")
   @GetMapping({"/list/{tagKey}"})
   // @PreAuthorize("@pms.hasPermission('admin_tagItem_key')")
   @Cacheable(value = {"tag_items"}, key = "#tagKey", unless = "#result.data.isEmpty()")
   public R<List<SysTagItem>> selectByKeys(@PathVariable("tagKey") String tagKey) {
      return R.ok(this.sysTagItemService.selectByKeys(tagKey));
   }

   @Inner
   @GetMapping({"/items/{tagKey}"})
   @Cacheable(value = {"tag_items"}, key = "#tagKey", unless = "#result.data.isEmpty()")
   public R<List<SysTagItem>> selectItemsByKeys(@PathVariable("tagKey") String tagKey) {
      return R.ok(this.sysTagItemService.selectByKeys(tagKey));
   }

   @Operation(summary = "通过id查询", description = "通过id查询(admin_tagItem_get)")
   @GetMapping({"/{id}"})
   // @PreAuthorize("@pms.hasPermission('admin_tagItem_get')")
   public R selectOne(@PathVariable String id) {
      return R.ok((SysTagItem)this.sysTagItemService.getById(id));
   }

   @PostMapping
   @SysLog("新增标签项")
   @Operation(summary = "新增标签项", description = "新增标签项 权限:admin_tagItem_add")
   // @PreAuthorize("@pms.hasPermission('admin_tagItem_add')")
   public R<SysTagItem> insert(@RequestBody @Validated SysTagItem sysTagItem) {
      return R.ok(this.sysTagItemService.saveTagItem(sysTagItem));
   }

   @PutMapping
   @SysLog("修改标签项")
   @Operation(summary = "修改标签项", description = "修改标签项,权限admin_tagItem_edit ")
   // @PreAuthorize("@pms.hasPermission('admin_tagItem_edit')")
   public R update(@RequestBody @Validated SysTagItem sysTagItem) {
      this.sysTagItemService.updateTagItem(sysTagItem);
      return R.ok();
   }

   @DeleteMapping({"{id}"})
   @SysLog("通过id删除标签项")
   @CacheEvict(value = {"tag_items"}, allEntries = true)
   @Operation(summary = "通过ID删除标签项", description = "通过ID删除标签项 权限：admin_tagItem_del")
   // @PreAuthorize("@pms.hasPermission('admin_tagItem_del')")
   public R delete(@PathVariable String id) {
      this.sysTagItemService.removeById(id);
      return R.ok();
   }
}
