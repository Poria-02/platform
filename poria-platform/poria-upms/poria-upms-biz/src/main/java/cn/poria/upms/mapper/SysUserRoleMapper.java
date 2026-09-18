package cn.poria.upms.mapper;

import cn.poria.upms.api.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
   Boolean deleteByUserId(@Param("userId") Long userId);
}
