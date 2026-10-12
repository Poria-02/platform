package cn.poria.message.controller.inner;


import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.entity.MsgMissionLog;
import cn.poria.message.service.MsgMissionLogService;
import cn.poria.message.vo.request.MissionLogModel;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * (MsgMissionLog)表控制层
 *
 * @author makejava
 * @since 2025-04-09 09:54:03
 */
@RestController
@RequestMapping("/inner/missionLog")
@Slf4j
public class MissionLogInnerController {
    /**
     * 服务对象
     */
    @Resource
    private MsgMissionLogService msgMissionLogService;

    @Operation(description = "添加健康宣教记录")
    @PostMapping("/message/log")
    @Inner
    public R saveMissionLog(@RequestBody MissionLogModel model) {
        MsgMissionLog msgMissionLog = new MsgMissionLog();
        BeanUtils.copyProperties(model, msgMissionLog);
        msgMissionLog.setStatus(0);
        this.msgMissionLogService.save(msgMissionLog);
        log.info("保存健康宣教记录:{}", msgMissionLog);
        return R.ok();
    }
}

