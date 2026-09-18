package cn.poria.upms.mapper;

import cn.poria.common.data.datascope.DataScope;
import cn.poria.upms.api.entity.SysTreeDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysTreeDictMapper extends BaseMapper<SysTreeDict> {
   List<SysTreeDict> selectDicts(String code, String name, Integer start, Integer limit, DataScope dataScope);

   Integer selectDictsCount(String code, String name, DataScope dataScope);

   SysTreeDict findById(@Param("id") String id, DataScope dataScope);

   void deleteTreeDict(@Param("list") String deleteList);

   SysTreeDict selectByCode(@Param("code") String code);
}
