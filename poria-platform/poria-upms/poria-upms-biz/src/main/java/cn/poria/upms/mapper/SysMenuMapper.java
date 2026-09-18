package cn.poria.upms.mapper;

import cn.poria.upms.api.entity.SysMenu;
import cn.poria.upms.api.vo.MenuVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
   List<MenuVO> listMenusByRoleId(@Param("roleId") Long roleId, @Param("platform") String platform);

   List<String> listPermissionsByRoleIds(String roleIds);
}
