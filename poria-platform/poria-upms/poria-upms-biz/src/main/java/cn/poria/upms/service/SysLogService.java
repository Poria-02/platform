package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysLog;
import cn.poria.upms.api.vo.PreLogVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface SysLogService extends IService<SysLog> {
   Boolean saveBatchLogs(List<PreLogVO> preLogVoList);
}
