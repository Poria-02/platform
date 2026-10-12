package cn.poria.message.dao;

import cn.poria.message.entity.MsgSmsLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信发送具体详情(MsgSmsLog)表数据库访问层
 *
 * @author makejava
 * @since 2020-05-20 15:03:13
 */
@Mapper
public interface MsgSmsLogDao extends BaseMapper<MsgSmsLog> {

}
