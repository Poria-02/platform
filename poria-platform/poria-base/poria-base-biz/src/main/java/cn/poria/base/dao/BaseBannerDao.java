package cn.poria.base.dao;

import cn.poria.base.entity.BaseBanner;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * banner主表(BaseBanner)表数据库访问层
 *
 * @author makejava
 * @since 2020-09-02 21:21:45
 */
@Mapper
public interface BaseBannerDao extends BaseMapper<BaseBanner> {

}
