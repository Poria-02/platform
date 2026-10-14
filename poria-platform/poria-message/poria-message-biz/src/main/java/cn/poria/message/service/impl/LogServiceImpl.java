package cn.poria.message.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import cn.poria.message.entity.MsgSmsLog;
import cn.poria.message.entity.MsgWxLog;
import cn.poria.message.service.LogService;
import cn.poria.message.service.MsgSmsLogService;
import cn.poria.message.service.MsgWxLogService;
import cn.poria.message.vo.request.LogQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    @Resource
    private MsgSmsLogService smsLogService;

    @Resource
    private MsgWxLogService wxLogService;

    @Override
    public IPage<MsgSmsLog> selectSmsLogs(LogQuery query) {

        Page page = new Page(query.getCurrent(),query.getSize());
        page.addOrder(OrderItem.desc("create_time"));

        LambdaQueryWrapper<MsgSmsLog> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(StrUtil.isNotBlank(query.getMobile()),MsgSmsLog::getPhone,query.getMobile());
        queryWrapper.between(query.getBeginDate()!=null && query.getEndDate()!=null,MsgSmsLog::getCreateTime,query.getBeginDate(),query.getEndDate());
        queryWrapper.like(StrUtil.isNotBlank(query.getContent()),MsgSmsLog::getContent,query.getContent());

        return smsLogService.page(page,queryWrapper);
    }

    @Override
    public IPage<MsgWxLog> selectWxTemplate(LogQuery query) {
        Page page = new Page(query.getCurrent(),query.getSize());
        page.addOrder(OrderItem.desc("create_time"));

        LambdaQueryWrapper<MsgWxLog> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(StrUtil.isNotBlank(query.getMobile()),MsgWxLog::getAccountId,query.getMobile());
        queryWrapper.between(query.getBeginDate()!=null && query.getEndDate()!=null,MsgWxLog::getCreateTime,query.getBeginDate(),query.getEndDate());
        queryWrapper.like(StrUtil.isNotBlank(query.getContent()),MsgWxLog::getContent,query.getContent());

        return wxLogService.page(page,queryWrapper);
    }
}
