package cn.poria.upms.mapper;

import cn.poria.upms.api.entity.SysDeptRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysDeptRelationMapper extends BaseMapper<SysDeptRelation> {
   void deleteDeptRelationsById(Long id);

   void updateDeptRelations(SysDeptRelation deptRelation);
}
