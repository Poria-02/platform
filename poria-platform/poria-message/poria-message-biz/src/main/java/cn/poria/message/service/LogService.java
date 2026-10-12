package cn.poria.message.service;

import cn.poria.message.entity.MsgDdLog;
import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.entity.MsgWxLog;
import cn.poria.message.vo.request.LogQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface LogService {

    /**
     * 分页查询钉钉消息
     * @param logQuery
     * @return
     */
    IPage<MsgDdLog> selectDingDingLogs(LogQuery logQuery);


    IPage<MsgSmsLog> selectSmsLogs(LogQuery query);

    IPage<MsgWxLog> selectWxTemplate(LogQuery query);
}
