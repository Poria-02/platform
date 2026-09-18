package cn.poria.upms.controller;

import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.upms.api.entity.SysTreeDict;
import cn.poria.upms.api.enums.DictionaryType;
import cn.poria.upms.api.vo.DictQueryModel;
import cn.poria.upms.api.vo.TreeDictModel;
import cn.poria.upms.api.vo.TreeDictVO;
import cn.poria.upms.service.SysTreeDictService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "分级字典管理")
@RequestMapping({"/treedict"})
public class SysTreeDictController {
   @Resource
   private SysTreeDictService sysTreeDictService;

   @Operation(summary = "字典类型")
   @GetMapping({"/types"})
   public R<List<DictionaryType>> types() {
      return R.ok(this.sysTreeDictService.getAllType());
   }

   @Operation(summary = "分页查询分级字典", description = "分页查询分级字典")
   @GetMapping({"/page"})
   public R<IPage<TreeDictVO>> page(@Validated DictQueryModel queryModel) {
      IPage<SysTreeDict> page = this.sysTreeDictService.page(queryModel);
      IPage<TreeDictVO> beanPage = page.convert((dict) -> {
         TreeDictVO treeDictVO = new TreeDictVO();
         BeanUtils.copyProperties(dict, treeDictVO);
         treeDictVO.setTypeName(DictionaryType.getName(dict.getType()));
         treeDictVO.setIsTree(dict.getIsTree() != 0);
         return treeDictVO;
      });
      return R.ok(beanPage);
   }

   @Operation(summary = "获取字典")
   @GetMapping({"/{id}"})
   public R<TreeDictVO> find(@PathVariable("id") @NotNull String id) {
      Optional<SysTreeDict> dictOpt = this.sysTreeDictService.findById(id);
      Assert.isTrue(dictOpt.isPresent(), "字典不存在", new Object[0]);
      TreeDictVO treeDictVO = new TreeDictVO();
      BeanUtils.copyProperties(dictOpt.get(), treeDictVO);
      treeDictVO.setTypeName(DictionaryType.getName(((SysTreeDict)dictOpt.get()).getType()));
      treeDictVO.setIsTree(((SysTreeDict)dictOpt.get()).getIsTree() != 0);
      return R.ok(treeDictVO);
   }

   @Operation(summary = "保存字典")
   @PostMapping({"/save"})
   @SysLog("保存字典")
   public R<String> save(@Validated @RequestBody @NotNull TreeDictVO bean) {
      bean.setType(DictionaryType.CUSTOMER.getId());
      Optional<SysTreeDict> sysTreeDict = this.sysTreeDictService.findByCode(bean.getCode());
      Assert.isTrue(!sysTreeDict.isPresent(), String.format("字典编码%s已存在", bean.getCode()), new Object[0]);
      SysTreeDict dict = new SysTreeDict();
      BeanUtils.copyProperties(bean, dict);
      dict.setIsTree(bean.getIsTree() ? 1 : 0);
      dict.setCreateBy(SecurityUtils.getUser().getId());
      dict.setCreateTime(new Date());
      this.sysTreeDictService.save(dict);
      return R.ok(dict.getId());
   }

   @Operation(summary = "修改字典")
   @PostMapping({"/update"})
   @SysLog("修改字典")
   public R updateDict(@Validated @RequestBody TreeDictModel req) {
      Optional<SysTreeDict> dictOpt = this.sysTreeDictService.findById(req.getId());
      Assert.isTrue(dictOpt.isPresent(), "字典不存在", new Object[0]);
      Assert.isTrue(((SysTreeDict)dictOpt.get()).getType() == DictionaryType.CUSTOMER.getId(), "系统字典不允许修改", new Object[0]);
      dictOpt = this.sysTreeDictService.findUserDictById(req.getId());
      SysTreeDict old = (SysTreeDict)dictOpt.get();
      old.setName(req.getName());
      old.setRemark(req.getRemark());
      old.setUpdateBy(SecurityUtils.getUser().getId());
      old.setUpdateTime(new Date());
      this.sysTreeDictService.updateById(old);
      return R.ok();
   }

   @Operation(summary = "删除字典")
   @SysLog("删除字典")
   @DeleteMapping({"/del/{id}"})
   public R<Boolean> delDict(@PathVariable @NotNull String id) {
      Optional<SysTreeDict> dictOpt = this.sysTreeDictService.findById(id);
      Assert.isTrue(dictOpt.isPresent(), "字典不存在", new Object[0]);
      Assert.isTrue(((SysTreeDict)dictOpt.get()).getType() == DictionaryType.CUSTOMER.getId(), "系统字典不允许删除", new Object[0]);
      dictOpt = this.sysTreeDictService.findUserDictById(id);
      this.sysTreeDictService.removeDictById(((SysTreeDict)dictOpt.get()).getId());
      return R.ok(Boolean.TRUE);
   }
}
