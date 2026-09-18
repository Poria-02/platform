package cn.poria.upms.service.impl;

import cn.hutool.json.JSONUtil;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.upms.api.dto.SensitiveInfo;
import cn.poria.upms.api.entity.SysSensitiveLog;
import cn.poria.upms.dao.SysSensitiveLogDao;
import cn.poria.upms.service.SysSensitiveLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysSensitiveLogServiceImpl extends ServiceImpl<SysSensitiveLogDao, SysSensitiveLog> implements SysSensitiveLogService {
   public void saveSensitiveLog(SensitiveInfo sensitiveInfo) {
      SysSensitiveLog sysSensitiveLog = new SysSensitiveLog();
      sysSensitiveLog.setCreateName(SecurityUtils.getUser().getUsername());
      sysSensitiveLog.setCreateBy(SecurityUtils.getSId());
      sysSensitiveLog.setUserType(sensitiveInfo.getUserType());
      sysSensitiveLog.setSensitiveInfo(JSONUtil.toJsonStr(sensitiveInfo));
      this.save(sysSensitiveLog);
   }
}
