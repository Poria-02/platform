package cn.poria.message.service;

import cn.poria.message.entity.MsgWxLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 短信发送具体详情(MsgWxLog)表服务接口
 *
 * @author makejava
 * @since 2020-08-10 15:19:50
 */
public interface MsgWxLogService extends IService<MsgWxLog> {

    void saveLogs(MsgWxLog wxLog);
}
