package cn.poria.message.dao;

import cn.poria.message.entity.MsgWxLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信发送具体详情(MsgWxLog)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-10 15:19:50
 */
@Mapper
public interface MsgWxLogDao extends BaseMapper<MsgWxLog> {

}
