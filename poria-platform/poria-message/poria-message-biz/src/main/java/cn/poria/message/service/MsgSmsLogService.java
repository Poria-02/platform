package cn.poria.message.service;

import cn.poria.message.entity.MsgSmsLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 短信发送具体详情(MsgSmsLog)表服务接口
 *
 * @author makejava
 * @since 2020-05-20 15:03:13
 */
public interface MsgSmsLogService extends IService<MsgSmsLog> {

    /**
     * 独立保存短信发送日志
     *
     * @param logs 按手机号生成的发送日志
     */
    void saveLogs(List<MsgSmsLog> logs);

}
