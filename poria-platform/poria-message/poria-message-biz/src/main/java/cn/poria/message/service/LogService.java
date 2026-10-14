package cn.poria.message.service;

import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.entity.MsgWxLog;
import cn.poria.message.vo.request.LogQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface LogService {

    IPage<MsgSmsLog> selectSmsLogs(LogQuery query);

    IPage<MsgWxLog> selectWxTemplate(LogQuery query);
}
