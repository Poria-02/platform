package cn.poria.message.controller.app;


import cn.poria.common.core.util.R;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.message.entity.MsgMissionLog;
import cn.poria.message.service.MsgMissionLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * (MsgMissionLog)表控制层
 *
 * @author makejava
 * @since 2025-04-09 09:54:03
 */
@RestController
@Tag(name = "【App】- 健康宣教记录")
@RequestMapping("/app/missionLog")
public class MissionLogController {
    /**
     * 服务对象
     */
    @Resource
    private MsgMissionLogService msgMissionLogService;

    @Operation(description = "分页查询本账号和指定账号的聊天记录")
    @GetMapping("/page")
    public R<Page> missionLogPage(Page page) {
        Page result = msgMissionLogService.page(page, new LambdaQueryWrapper<MsgMissionLog>()
                .eq(MsgMissionLog::getToUser, SecurityUtils.getSId()).orderByDesc(MsgMissionLog::getCreateTime)
        );
        return R.ok(result);
    }


}

