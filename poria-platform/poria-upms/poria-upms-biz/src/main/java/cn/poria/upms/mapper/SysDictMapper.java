package cn.poria.upms.mapper;

import cn.poria.upms.api.entity.SysDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysDictMapper extends BaseMapper<SysDict> {
   void deleteDict(@Param("list") String deleteList);
}
