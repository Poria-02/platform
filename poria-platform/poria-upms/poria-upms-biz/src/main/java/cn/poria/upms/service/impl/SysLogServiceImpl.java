package cn.poria.upms.service.impl;

import cn.poria.upms.api.entity.SysLog;
import cn.poria.upms.api.vo.request.log.PreLogModel;
import cn.poria.upms.mapper.SysLogMapper;
import cn.poria.upms.service.SysLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    public Boolean saveBatchLogs(List<PreLogModel> preLogModels) {
        List<SysLog> sysLogs = preLogModels.stream().map((pre) -> {
            SysLog log = new SysLog();
            log.setType("9");
            log.setTitle(pre.getInfo());
            log.setException(pre.getStack());
            log.setParams(pre.getMessage());
            log.setCreateTime(LocalDateTime.now());
            log.setRequestUri(pre.getUrl());
            log.setCreateBy(pre.getUser());
            return log;
        }).collect(Collectors.toList());
        return this.saveBatch(sysLogs);
    }
}
