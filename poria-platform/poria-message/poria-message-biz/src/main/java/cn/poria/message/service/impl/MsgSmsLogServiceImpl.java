package cn.poria.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.message.dao.MsgSmsLogDao;
import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.service.MsgSmsLogService;
import org.springframework.stereotype.Service;

/**
 * 短信发送具体详情(MsgSmsLog)表服务实现类
 *
 * @author makejava
 * @since 2020-05-20 15:03:13
 */
@Service
public class MsgSmsLogServiceImpl extends ServiceImpl<MsgSmsLogDao, MsgSmsLog> implements MsgSmsLogService {

}
