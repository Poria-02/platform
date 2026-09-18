package cn.poria.upms.mapper;

import cn.poria.common.data.datascope.DataScope;
import cn.poria.upms.api.entity.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {
   List<SysDept> listDepts(DataScope dataScope);

   SysDept getOrgByDeptId(@Param("deptId") Long deptId);
}
