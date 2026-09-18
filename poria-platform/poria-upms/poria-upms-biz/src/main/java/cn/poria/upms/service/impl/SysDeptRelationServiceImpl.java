package cn.poria.upms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.poria.upms.api.entity.SysDept;
import cn.poria.upms.api.entity.SysDeptRelation;
import cn.poria.upms.mapper.SysDeptRelationMapper;
import cn.poria.upms.service.SysDeptRelationService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysDeptRelationServiceImpl extends ServiceImpl<SysDeptRelationMapper, SysDeptRelation> implements SysDeptRelationService {
   private final SysDeptRelationMapper sysDeptRelationMapper;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void insertDeptRelation(SysDept sysDept) {
      SysDeptRelation condition = new SysDeptRelation();
      condition.setDescendant(sysDept.getParentId());
      List<SysDeptRelation> relationList = this.sysDeptRelationMapper.selectList(Wrappers.<SysDeptRelation>lambdaQuery().eq(SysDeptRelation::getDescendant, sysDept.getParentId())).stream().peek((relation) -> relation.setDescendant(sysDept.getDeptId())).collect(Collectors.toList());
      if (CollUtil.isNotEmpty(relationList)) {
         this.saveBatch(relationList);
      }

      SysDeptRelation own = new SysDeptRelation();
      own.setDescendant(sysDept.getDeptId());
      own.setAncestor(sysDept.getDeptId());
      this.sysDeptRelationMapper.insert(own);
   }

   public void deleteAllDeptRealtion(Long id) {
      ((SysDeptRelationMapper)this.baseMapper).deleteDeptRelationsById(id);
   }

   public void updateDeptRealtion(SysDeptRelation relation) {
      ((SysDeptRelationMapper)this.baseMapper).updateDeptRelations(relation);
   }

   public SysDeptRelationServiceImpl(final SysDeptRelationMapper sysDeptRelationMapper) {
      this.sysDeptRelationMapper = sysDeptRelationMapper;
   }

}
