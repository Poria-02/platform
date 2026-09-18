package cn.poria.base.dao;

import cn.poria.base.entity.BaseAppVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * (BaseAppVersion)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-31 22:14:55
 */
@Mapper
public interface BaseAppVersionDao extends BaseMapper<BaseAppVersion> {

}
