package cn.poria.upms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.poria.common.data.datascope.DataScope;
import cn.poria.upms.api.dto.DeptTree;
import cn.poria.upms.api.entity.SysDept;
import cn.poria.upms.api.entity.SysDeptRelation;
import cn.poria.upms.api.vo.TreeUtil;
import cn.poria.upms.mapper.SysDeptMapper;
import cn.poria.upms.service.SysDeptRelationService;
import cn.poria.upms.service.SysDeptService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

   private final SysDeptRelationService sysDeptRelationService;

   @Transactional(rollbackFor = {Exception.class})
   public SysDept saveDept(SysDept dept) {
      SysDept sysDept = new SysDept();
      BeanUtils.copyProperties(dept, sysDept);
      this.save(sysDept);
      this.sysDeptRelationService.insertDeptRelation(sysDept);
      return sysDept;
   }

   @Transactional(rollbackFor = {Exception.class})
   public Boolean removeDeptById(Long id) {
      List<Long> idList = this.sysDeptRelationService.list(Wrappers.<SysDeptRelation>lambdaQuery().eq(SysDeptRelation::getAncestor, id)).stream().map(SysDeptRelation::getDescendant).collect(Collectors.toList());
      if (CollUtil.isNotEmpty(idList)) {
         this.removeByIds(idList);
      }
      this.sysDeptRelationService.deleteAllDeptRealtion(id);
      return Boolean.TRUE;
   }

   @Transactional(rollbackFor = {Exception.class})
   public Boolean updateDeptById(SysDept sysDept) {
      this.updateById(sysDept);
      SysDeptRelation relation = new SysDeptRelation();
      relation.setAncestor(sysDept.getParentId());
      relation.setDescendant(sysDept.getDeptId());
      this.sysDeptRelationService.updateDeptRealtion(relation);
      return Boolean.TRUE;
   }

   public SysDept getOrgByDeptId(Long deptId) {
      return (this.baseMapper).getOrgByDeptId(deptId);
   }

   public List<DeptTree> selectTree() {
      return this.getDeptTree((this.baseMapper).listDepts(new DataScope()));
   }

   public List<DeptTree> selectTree(Long departId) {
      DataScope dataScope = new DataScope();
      dataScope.getDeptList().add(departId);
      List<SysDeptRelation> list = this.sysDeptRelationService.list(Wrappers.<SysDeptRelation>lambdaQuery().eq(SysDeptRelation::getAncestor, departId));
      if (CollectionUtil.isNotEmpty(list)) {
         dataScope.getDeptList().addAll(list.stream().map(SysDeptRelation::getDescendant).collect(Collectors.toList()));
      }

      return this.getDeptTree((this.baseMapper).listDepts(dataScope));
   }

   private List<DeptTree> getDeptTree(List<SysDept> depts) {
      List<DeptTree> treeList = depts.stream().filter((dept) -> !dept.getDeptId().equals(dept.getParentId())).sorted(Comparator.comparingInt(SysDept::getSort)).map((dept) -> {
         DeptTree node = new DeptTree();
         node.setId(dept.getDeptId());
         node.setParentId(dept.getParentId());
         node.setName(dept.getName());
         return node;
      }).collect(Collectors.toList());
      log.info("treeList:{}", treeList);
      if (CollUtil.isEmpty(treeList)) {
         return new ArrayList<>();
      }
      return TreeUtil.build(treeList, (depts.stream().min(Comparator.comparingLong(SysDept::getDeptId)).get()).getParentId());
   }

   public SysDeptServiceImpl(final SysDeptRelationService sysDeptRelationService) {
      this.sysDeptRelationService = sysDeptRelationService;
   }

}
