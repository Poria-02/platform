package cn.poria.upms.mapper;

import cn.poria.upms.api.entity.SysDictItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {
   void deleteDictItem(@Param("list") String deleteList);
}
