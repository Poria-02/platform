package cn.poria.upms.mapper;

import cn.poria.upms.api.entity.SysTreeDictItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysTreeDictItemMapper extends BaseMapper<SysTreeDictItem> {
   void deleteTreeDictItem(@Param("list") String deleteList);
}
