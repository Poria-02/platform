package cn.poria.message.service.impl;

import cn.poria.common.core.util.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.message.dao.MsgSmsLogDao;
import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.service.MsgSmsLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 短信发送具体详情(MsgSmsLog)表服务实现类
 *
 * @author makejava
 * @since 2020-05-20 15:03:13
 */
@Service
public class MsgSmsLogServiceImpl extends ServiceImpl<MsgSmsLogDao, MsgSmsLog> implements MsgSmsLogService {

    /**
     * 在独立事务中保存日志，避免发送失败后被业务事务回滚
     *
     * @param logs 按手机号生成的发送日志
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLogs(List<MsgSmsLog> logs) {
        Assert.isTrue(this.saveBatch(logs), "短信发送日志保存失败");
    }

}
