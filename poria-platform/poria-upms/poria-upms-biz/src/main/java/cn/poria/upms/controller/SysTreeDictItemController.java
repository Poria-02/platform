package cn.poria.upms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.upms.api.entity.SysTreeDict;
import cn.poria.upms.api.entity.SysTreeDictItem;
import cn.poria.upms.api.enums.DictionaryType;
import cn.poria.upms.api.vo.ExportDictModel;
import cn.poria.upms.api.vo.TreeDictItemModel;
import cn.poria.upms.api.vo.TreeDictItemVo;
import cn.poria.upms.service.SysTreeDictItemService;
import cn.poria.upms.service.SysTreeDictService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
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
@Tag(name = "分级字典管理")
@RequestMapping({"/treedict/item"})
public class SysTreeDictItemController {
   @Resource
   private SysTreeDictService treeDictService;
   @Resource
   private SysTreeDictItemService treeDictItemService;

   @Inner(false)
   @Operation(summary = "查询单个字典项", description = "查询单个字典项")
   @GetMapping({"/getItem/{dictId}/{value}"})
   public R<TreeDictItemVo> getDictItem(@PathVariable("dictId") String dictId, @PathVariable("value") String value) {
      SysTreeDictItem dictItem = (SysTreeDictItem)this.treeDictItemService.getOne((Wrapper)((new LambdaQueryWrapper<SysTreeDictItem>()).eq(SysTreeDictItem::getDictId, dictId)).eq(SysTreeDictItem::getValue, value));
      TreeDictItemVo treeDictItemVo = new TreeDictItemVo();
      BeanUtil.copyProperties(dictItem, treeDictItemVo, new String[0]);
      return R.ok(treeDictItemVo);
   }

   @Inner(false)
   @Operation(summary = "查询字典项(返回树)", description = "查询字典项(返回树)")
   @GetMapping({"/detail/{code}"})
   @Cacheable(value = {"tree_dict_details_tree"}, key = "#code", unless = "#result.data.isEmpty()")
   public R<List<TreeDictItemVo>> dictItemTrees(@PathVariable("code") String code) {
      Optional<SysTreeDict> dictOpt = this.treeDictService.findByCode(code);
      Assert.isTrue(dictOpt.isPresent(), "字典项不存在", new Object[0]);
      List<SysTreeDictItem> list = this.treeDictService.findItems(((SysTreeDict)dictOpt.get()).getId(), (String)null);
      List<TreeDictItemVo> beans = (List)list.stream().map((item) -> this.toDictItemBean(item)).collect(Collectors.toList());
      if (((SysTreeDict)dictOpt.get()).getIsTree() == 1) {
         beans = this.toTree(beans);
      }

      return R.ok(beans);
   }

   @Inner(false)
   @Operation(summary = "查询字典项(返回列表)", description = "查询字典项(返回列表)")
   @GetMapping({"/list/{code}"})
   @Cacheable(value = {"tree_dict_details_list"}, key = "#code", unless = "#result.data.isEmpty()")
   public R<List<TreeDictItemVo>> dictItems(@PathVariable("code") String code) {
      Optional<SysTreeDict> dictOpt = this.treeDictService.findByCode(code);
      Assert.isTrue(dictOpt.isPresent(), "字典项不存在", new Object[0]);
      List<SysTreeDictItem> list = this.treeDictService.findItems(((SysTreeDict)dictOpt.get()).getId(), (String)null);
      List<TreeDictItemVo> beans = (List)list.stream().map((item) -> this.toDictItemBean(item)).collect(Collectors.toList());
      return R.ok(beans);
   }

   @Operation(description = "添加字典项")
   @PostMapping({"/add"})
   @CacheEvict(value = {"tree_dict_details_list", "tree_dict_details_tree"}, allEntries = true)
   public R<SysTreeDictItem> add(@Validated @RequestBody TreeDictItemModel model) {
      Optional<SysTreeDict> dictOpt = this.treeDictService.findById(model.getDictId());
      Assert.isTrue(dictOpt.isPresent(), "字典不存在", new Object[0]);
      Assert.isTrue(((SysTreeDict)dictOpt.get()).getType() == DictionaryType.CUSTOMER.getId(), "系统字典不允许修改", new Object[0]);
      SysTreeDictItem local = (SysTreeDictItem)this.treeDictItemService.getOne((Wrapper)((QueryWrapper)(new QueryWrapper()).eq("dict_id", model.getDictId())).eq("value", model.getValue()));
      Assert.isNull(local, "该字典项已存在:" + model.getValue(), new Object[0]);
      SysTreeDictItem item = this.toDictItem(model);
      item.setCreateBy(SecurityUtils.getUser().getId());
      item.setCreateTime(new Date());
      this.treeDictService.saveItem(item);
      return R.ok(item);
   }

   @Operation(description = "更新字典项")
   @PutMapping({"/update"})
   @CacheEvict(value = {"tree_dict_details_list", "tree_dict_details_tree"}, allEntries = true)
   public R<Boolean> updateItem(@Validated @RequestBody TreeDictItemModel req) {
      Optional<SysTreeDict> dictOpt = this.treeDictService.findById(req.getDictId());
      Assert.isTrue(dictOpt.isPresent(), "字典不存在", new Object[0]);
      Assert.isTrue(((SysTreeDict)dictOpt.get()).getType() == DictionaryType.CUSTOMER.getId(), "系统字典不允许修改", new Object[0]);
      Optional<SysTreeDictItem> itemOpt = this.treeDictService.findItem(req.getId());
      Assert.isTrue(itemOpt.isPresent(), "不存在", new Object[0]);
      SysTreeDictItem old = (SysTreeDictItem)itemOpt.get();
      old.setName(req.getName());
      old.setSimpleName(req.getSimpleName());
      old.setRemark(req.getRemark());
      if (!old.getValue().equals(req.getValue())) {
         SysTreeDictItem local = (SysTreeDictItem)this.treeDictItemService.getOne((Wrapper)((QueryWrapper)(new QueryWrapper()).eq("dict_id", req.getDictId())).eq("value", req.getValue()));
         Assert.isNull(local, "该字典项已存在:" + req.getValue(), new Object[0]);
         old.setValue(req.getValue());
      }

      old.setExt1(req.getExt1());
      old.setUpdateTime(new Date());
      old.setUpdateBy(SecurityUtils.getUser().getId());
      old.setSort(req.getSort());
      this.treeDictService.updateItem(old);
      return R.ok(Boolean.TRUE);
   }

   @Operation(description = "删除字典项")
   @DeleteMapping({"/del/{itemId}"})
   @CacheEvict(value = {"tree_dict_details_list", "tree_dict_details_tree"}, allEntries = true)
   public R<Boolean> delItem(@PathVariable("itemId") String itemId) {
      Optional<SysTreeDictItem> itemOpt = this.treeDictService.findItem(itemId);
      Assert.isTrue(itemOpt.isPresent(), "字典项不存在", new Object[0]);
      Optional<SysTreeDict> dictOpt = this.treeDictService.findById(((SysTreeDictItem)itemOpt.get()).getDictId());
      Assert.isTrue(dictOpt.isPresent(), "字典不存在", new Object[0]);
      Assert.isTrue(((SysTreeDict)dictOpt.get()).getType() == DictionaryType.CUSTOMER.getId(), "系统字典不允许修改", new Object[0]);
      this.treeDictItemService.removeTreeItemsById(((SysTreeDictItem)itemOpt.get()).getId());
      return R.ok(Boolean.TRUE);
   }

   @Operation(description = "查询树形字典")
   @GetMapping({"/find_tree_items/{code}/{pid}"})
   public R<List<TreeDictItemVo>> getTreeItems(@Parameter(name = "字典CODE") @PathVariable("code") String code, @Parameter(name = "父节点ID,如果为0代表一级节点") @PathVariable("pid") String pid) {
      Optional<SysTreeDict> dictOpt = this.treeDictService.findByCode(code);
      Assert.isTrue(dictOpt.isPresent(), "不存在", new Object[0]);
      List<SysTreeDictItem> items = this.treeDictService.findItemsByPid(((SysTreeDict)dictOpt.get()).getId(), pid);
      List<TreeDictItemVo> beans = (List)items.stream().map((item) -> this.toDictItemBean(item)).collect(Collectors.toList());
      return R.ok(beans);
   }

   @Operation(summary = "查询所有城市", description = "查询所有的市")
   @GetMapping({"/cityList"})
   @Cacheable({"tree_dict_details_city"})
   public R<List<TreeDictItemVo>> cityList() {
      List<SysTreeDictItem> citys = this.treeDictItemService.list((Wrapper)((QueryWrapper)(new QueryWrapper()).eq("dict_id", "china_positon")).eq("simple_name", "市"));
      return R.ok((List)citys.stream().map((vo) -> this.toDictItemBean(vo)).collect(Collectors.toList()));
   }

   @Schema(description = "查询上级字典项")
   @Inner
   @GetMapping({"/parent/{dictCode}/{value}"})
   public R<TreeDictItemVo> getParentDictItem(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value) {
      SysTreeDictItem sysTreeDictItem = this.treeDictItemService.getParentDictItem(dictCode, value);
      TreeDictItemVo vo = new TreeDictItemVo();
      BeanUtils.copyProperties(sysTreeDictItem, vo);
      return R.ok(vo);
   }

   @Schema(description = "查询同级字典项")
   @Inner
   @GetMapping({"/peer/{dictCode}/{value}"})
   public R<TreeDictItemVo> getPeerDictItem(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value) {
      SysTreeDictItem sysTreeDictItem = this.treeDictItemService.getPeerDictItem(dictCode, value);
      TreeDictItemVo vo = new TreeDictItemVo();
      BeanUtils.copyProperties(sysTreeDictItem, vo);
      return R.ok(vo);
   }

   private List toTree(List<TreeDictItemVo> beans) {
      Map<String, TreeDictItemVo> map = (Map)beans.stream().collect(Collectors.toMap(TreeDictItemVo::getId, (TreeDictItemVo) -> TreeDictItemVo));

      for(TreeDictItemVo b : beans) {
         if (StrUtil.isNotBlank(b.getPid())) {
            TreeDictItemVo parent = (TreeDictItemVo)map.get(b.getPid());
            if (parent != null) {
               if (parent.getChilds() == null) {
                  parent.setChilds(new ArrayList());
               }

               parent.getChilds().add(b);
            }
         }
      }

      List<TreeDictItemVo> tree = new ArrayList();

      for(TreeDictItemVo b : beans) {
         if ("0".equals(b.getPid())) {
            tree.add(b);
         }
      }

      return tree;
   }

   private TreeDictItemVo toDictItemBean(SysTreeDictItem item) {
      TreeDictItemVo bean = new TreeDictItemVo();
      BeanUtils.copyProperties(item, bean);
      return bean;
   }

   private SysTreeDictItem toDictItem(TreeDictItemModel model) {
      SysTreeDictItem item = new SysTreeDictItem();
      BeanUtils.copyProperties(model, item);
      return item;
   }

   @SysLog("导出字典")
   @PostMapping({"/export"})
   @Operation(summary = "导出字典", description = "导出字典")
   public void exportDict(@RequestBody ExportDictModel exportDictModel, HttpServletResponse response) throws IOException {
      this.treeDictItemService.exportDict(exportDictModel, response);
   }

   @SysLog("导入字典")
   @PostMapping({"/import"})
   @Operation(summary = "导入字典", description = "导入字典")
   public void importtDict(@RequestParam("file") MultipartFile file) throws IOException {
      this.treeDictItemService.importDict(file);
   }

   @Inner
   @GetMapping({"/findByValue/{value}"})
   @Operation(summary = "查询字典职称", description = "查询字典职称")
   public R<TreeDictItemVo> findAuthenticate(@PathVariable("value") String value) {
      SysTreeDictItem treeDictItem = (SysTreeDictItem)this.treeDictItemService.getOne((Wrapper)(new LambdaQueryWrapper<SysTreeDictItem>()).eq(SysTreeDictItem::getValue, value));
      if (treeDictItem != null) {
         TreeDictItemVo vo = new TreeDictItemVo();
         BeanUtils.copyProperties(treeDictItem, vo);
         return R.ok(vo);
      } else {
         return R.ok();
      }
   }

}
