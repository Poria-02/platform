package cn.poria.base.dao;

import cn.poria.base.entity.BaseFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件管理表(BaseFile)表数据库访问层
 *
 * @author makejava
 * @since 2021-07-29 10:37:17
 */
@Mapper
public interface BaseFileDao extends BaseMapper<BaseFile> {

}
