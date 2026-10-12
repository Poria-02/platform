package cn.poria.message.dao;

import cn.poria.message.vo.request.LogQuery;
import cn.poria.message.entity.MsgDdLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 短信发送具体详情(MsgDdLog)表数据库访问层
 *
 * @author makejava
 * @since 2020-05-21 17:17:49
 */
@Mapper
public interface MsgDdLogDao extends BaseMapper<MsgDdLog> {

    IPage<MsgDdLog> selectLogs(IPage<MsgDdLog> page,@Param("logQuery") LogQuery logQuery);

}
