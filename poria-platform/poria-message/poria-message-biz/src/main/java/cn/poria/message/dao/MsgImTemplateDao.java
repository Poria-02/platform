package cn.poria.message.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.poria.message.entity.MsgImTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * IM消息模板(MsgImTemplate)表数据库访问层
 *
 * @author wuning
 * @since 2025-05-07 10:47:07
 */
@Mapper
public interface MsgImTemplateDao extends BaseMapper<MsgImTemplate> {

}
