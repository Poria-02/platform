package cn.poria.upms.controller;

import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysDict;
import cn.poria.upms.api.entity.SysDictItem;
import cn.poria.upms.api.vo.ExportDictModel;
import cn.poria.upms.service.SysDictItemService;
import cn.poria.upms.service.SysDictService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/dict"})
@Tag(name = "dict", description = "字典管理模块")
public class SysDictController {

   private final SysDictService sysDictService;

   private final SysDictItemService sysDictItemService;

   @GetMapping({"/{id}"})
   public R getById(@PathVariable String id) {
      return R.ok((SysDict)this.sysDictService.getById(id));
   }

   @GetMapping({"/page"})
   public R<IPage> getDictPage(Page page, SysDict sysDict) {
      return R.ok(this.sysDictService.page(page, (Wrapper)Wrappers.query(sysDict).orderByDesc("create_time")));
   }

   @Inner(false)
   @GetMapping({"/type/{type}"})
   @Cacheable(value = {"dict_details"}, key = "#type", unless = "#result.data.isEmpty()")
   public R getDictByType(@PathVariable String type) {
      return R.ok(this.sysDictItemService.list((Wrapper)Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getType, type)));
   }

   @SysLog("添加字典")
   @Operation(summary = "添加字典", description = "添加字典（sys_dict_add）")
   @PostMapping
   // @PreAuthorize("@pms.hasPermission('sys_dict_add')")
   public R save(@RequestBody @Valid SysDict sysDict) {
      return R.ok(this.sysDictService.save(sysDict));
   }

   @SysLog("删除字典")
   @DeleteMapping({"/{id}"})
   @Operation(summary = "删除字典", description = "删除字典（sys_dict_del）")
   // @PreAuthorize("@pms.hasPermission('sys_dict_del')")
   public R removeById(@PathVariable String id) {
      return this.sysDictService.removeDict(id);
   }

   @PutMapping
   @SysLog("修改字典")
   @Operation(summary = "修改字典", description = "修改字典（sys_dict_edit）")
   // @PreAuthorize("@pms.hasPermission('sys_dict_edit')")
   public R updateById(@RequestBody @Valid SysDict sysDict) {
      return this.sysDictService.updateDict(sysDict);
   }

   @GetMapping({"/item/page"})
   public R getSysDictItemPage(Page page, SysDictItem sysDictItem) {
      page.addOrder(new OrderItem[]{OrderItem.desc("sort")});
      return R.ok((Page)this.sysDictItemService.page(page, (((new LambdaQueryWrapper<SysDictItem>()).eq(sysDictItem.getDictId() != null, SysDictItem::getDictId, sysDictItem.getDictId())).eq(StrUtil.isNotBlank(sysDictItem.getType()), SysDictItem::getType, sysDictItem.getType())).like(StrUtil.isNotBlank(sysDictItem.getLabel()), SysDictItem::getLabel, sysDictItem.getLabel())));
   }

   @Inner
   @Hidden
   @GetMapping({"/items/{type}"})
   public R<List<SysDictItem>> getSysDictItems(@PathVariable("type") String type) {
      return R.ok(this.sysDictItemService.list((Wrapper)(new QueryWrapper()).eq("type", type)));
   }

   @Inner
   @Hidden
   @GetMapping({"/item/{dictCode}/{value}"})
   public R<SysDictItem> findDictItem(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value) {
      return R.ok(this.sysDictItemService.findDictItem(dictCode, value));
   }

   @Operation(summary = "查询指定字典项", description = "查询指定字典项")
   @GetMapping({"/api/item/{dictCode}/{value}"})
   public R<SysDictItem> findItemValue(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value) {
      return R.ok(this.sysDictItemService.findDictItem(dictCode, value));
   }

   @GetMapping({"/item/{id}"})
   public R getDictItemById(@PathVariable("id") String id) {
      return R.ok((SysDictItem)this.sysDictItemService.getById(id));
   }

   @SysLog("新增字典项")
   @PostMapping({"/item"})
   @CacheEvict(value = {"dict_details"}, allEntries = true)
   public R save(@RequestBody SysDictItem sysDictItem) {
      return R.ok(this.sysDictItemService.save(sysDictItem));
   }

   @SysLog("修改字典项")
   @PutMapping({"/item"})
   public R updateById(@RequestBody SysDictItem sysDictItem) {
      return this.sysDictItemService.updateDictItem(sysDictItem);
   }

   @SysLog("删除字典项")
   @DeleteMapping({"/item/{id}"})
   public R removeDictItemById(@PathVariable String id) {
      return this.sysDictItemService.removeDictItem(id);
   }

   @SysLog("导出字典")
   @PostMapping({"/export"})
   @Operation(summary = "导出字典", description = "导出字典")
   public void exportDict(@RequestBody ExportDictModel exportDictModel, HttpServletResponse response) throws IOException {
      this.sysDictService.exportDict(exportDictModel, response);
   }

   @SysLog("导入字典")
   @PostMapping({"/import"})
   @Operation(summary = "导入字典", description = "导入字典")
   public R importtDict(@RequestParam("file") MultipartFile file) throws IOException {
      this.sysDictService.importDict(file);
      return R.ok();
   }

   public SysDictController(final SysDictService sysDictService, final SysDictItemService sysDictItemService) {
      this.sysDictService = sysDictService;
      this.sysDictItemService = sysDictItemService;
   }

}
