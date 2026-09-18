package cn.poria.upms.mapper;

import cn.poria.common.data.datascope.DataScope;
import cn.poria.upms.api.dto.UserDTO;
import cn.poria.upms.api.entity.SysUser;
import cn.poria.upms.api.vo.UserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
   UserVO getUserVoByUsername(String username);

   IPage<UserVO> getUserVosPage(Page page, @Param("query") UserDTO userDTO, DataScope dataScope);

   UserVO getUserVoById(Integer id);
}
