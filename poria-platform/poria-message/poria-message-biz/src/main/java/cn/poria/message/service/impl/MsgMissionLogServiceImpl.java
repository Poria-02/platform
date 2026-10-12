package cn.poria.message.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.message.dao.MsgMissionLogDao;
import cn.poria.message.entity.MsgMissionLog;
import cn.poria.message.service.MsgMissionLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * (MsgMissionLog)表服务实现类
 *
 * @author makejava
 * @since 2025-04-09 09:54:04
 */
@Service("msgMissionLogService")
@Slf4j
public class MsgMissionLogServiceImpl extends ServiceImpl<MsgMissionLogDao, MsgMissionLog> implements MsgMissionLogService {


    /**
    * @Author: wuyifan
    * @Date: 2025/4/9 11:29
    * @Description: 健康宣教修改已读事件
    */
    @Transactional
    @KafkaListener(topics = "health_education_article_read_count", groupId = "health_mission_read")
    public void saveHealthPoint(final String message) {

        log.info("监听健康宣教文章阅读状态{}", message);
        JSONObject entries = JSONUtil.parseObj(message);
        String businessId = entries.getStr("businessId");
        String accountId = entries.getStr("accountId");
        if (StringUtils.isBlank(businessId)) {
            throw new IllegalArgumentException("businessId 为空");
        }
        List<MsgMissionLog> logs = this.list(new LambdaQueryWrapper<MsgMissionLog>().eq(MsgMissionLog::getToUser, accountId).eq(MsgMissionLog::getType, 2).eq(MsgMissionLog::getStatus, 0));
        if (ObjectUtils.isNotEmpty(logs)){
            ArrayList<String> mission = new ArrayList<>();
            logs.forEach(info->{
                String o = JSONUtil.parseObj(JSONUtil.parseObj(info.getContent()).getStr("content")).getStr("id");
                if (ObjectUtil.equals(o, businessId)){
                    mission.add(info.getId());
                }
            });
            if (ObjectUtil.isNotEmpty(mission)){
                this.update(new LambdaUpdateWrapper<MsgMissionLog>()
                        .set(MsgMissionLog::getStatus, 1)
                        .in(MsgMissionLog::getId, mission));
                log.info("更新健康宣教文章阅读状态{}, 健康宣教id: {},更新id{}", accountId, businessId,mission);
            }
        }
    }

}

