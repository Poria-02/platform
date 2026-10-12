package cn.poria.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.message.dao.MsgYxxhLogDao;
import cn.poria.message.entity.MsgYxxhLog;
import cn.poria.message.service.MsgYxxhLogService;
import org.springframework.stereotype.Service;

/**
 * 云信小号通话记录(MsgYxxhLog)表服务实现类
 *
 * @author wuyifan
 * @since 2024-03-26 10:23:00
 */
@Service("msgYxxhLogService")
public class MsgYxxhLogServiceImpl extends ServiceImpl<MsgYxxhLogDao, MsgYxxhLog> implements MsgYxxhLogService {

}

