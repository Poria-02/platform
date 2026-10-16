package cn.poria.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.common.core.util.Assert;
import cn.poria.message.dao.MsgWxLogDao;
import cn.poria.message.entity.MsgWxLog;
import cn.poria.message.service.MsgWxLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 短信发送具体详情(MsgWxLog)表服务实现类
 *
 * @author makejava
 * @since 2020-08-10 15:19:50
 */
@Service
public class MsgWxLogServiceImpl extends ServiceImpl<MsgWxLogDao, MsgWxLog> implements MsgWxLogService {

    @Override
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void saveLogs(MsgWxLog wxLog) {
        Assert.isTrue(this.save(wxLog), "微信消息发送日志保存失败");
    }
}
