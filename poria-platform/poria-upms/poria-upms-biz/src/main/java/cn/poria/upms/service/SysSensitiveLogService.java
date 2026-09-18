package cn.poria.upms.service;

import cn.poria.upms.api.dto.SensitiveInfo;
import cn.poria.upms.api.entity.SysSensitiveLog;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysSensitiveLogService extends IService<SysSensitiveLog> {
   void saveSensitiveLog(SensitiveInfo sensitiveInfo);
}
