package cn.poria.upms.mapper;

import cn.poria.upms.api.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
   List<SysRole> listRolesByUserId(Long userId);

   Integer existSysRole(@Param("model") SysRole sysRole);
}
