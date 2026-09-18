package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysDept;
import cn.poria.upms.api.entity.SysDeptRelation;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysDeptRelationService extends IService<SysDeptRelation> {
   void insertDeptRelation(SysDept sysDept);

   void deleteAllDeptRealtion(Long id);

   void updateDeptRealtion(SysDeptRelation relation);
}
