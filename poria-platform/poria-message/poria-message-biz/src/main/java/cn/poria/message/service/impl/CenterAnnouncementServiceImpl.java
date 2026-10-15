package cn.poria.message.service.impl;

import cn.poria.common.core.util.Assert;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.message.dao.MsgAnnouncementDao;
import cn.poria.message.entity.MsgAnnouncement;
import cn.poria.message.enums.AnnouncementStatus;
import cn.poria.message.enums.PendingTaskType;
import cn.poria.message.service.CenterAnnouncementService;
import cn.poria.message.service.PendingTaskManager;
import cn.poria.message.vo.request.PendingTaskModel;
import cn.poria.message.vo.request.center.CenterDraftModel;
import cn.poria.message.vo.request.center.CenterQueryModel;
import cn.poria.message.vo.request.center.CenterSendModel;
import cn.poria.message.vo.request.center.CenterStatusModel;
import cn.poria.message.vo.response.center.AnnouncementVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 管理平台公示公告服务实现类，阅读状态由公告类型待办维护
 */
@Service
public class CenterAnnouncementServiceImpl implements CenterAnnouncementService {

    /**
     * 公示公告数据库访问对象
     */
    @Resource
    private MsgAnnouncementDao msgAnnouncementDao;

    /**
     * 待办任务管理对象
     */
    @Resource
    private PendingTaskManager pendingTaskManager;

    @Override
    public IPage<AnnouncementVo> page(CenterQueryModel query) {
        return msgAnnouncementDao.selectPage(new Page<>(query.getCurrent(), query.getSize()),
                new LambdaQueryWrapper<MsgAnnouncement>()
                        .eq(MsgAnnouncement::getStatus, query.getStatus())
                        .and(StringUtils.hasText(query.getKeyword()), condition -> condition.like(MsgAnnouncement::getTitle, query.getKeyword())
                                .or().like(MsgAnnouncement::getContent, query.getKeyword()))
                        .orderByDesc(MsgAnnouncement::getPublishTime).orderByDesc(MsgAnnouncement::getCreateTime).orderByDesc(MsgAnnouncement::getId))
                .convert(notice -> {
                    AnnouncementVo vo = new AnnouncementVo();
                    BeanUtils.copyProperties(notice, vo);
                    return vo;
                });
    }

    @Override
    public AnnouncementVo detail(String id) {
        MsgAnnouncement notice = msgAnnouncementDao.selectById(id);
        Assert.notNull(notice, "公告不存在");
        AnnouncementVo vo = new AnnouncementVo();
        BeanUtils.copyProperties(notice, vo);
        return vo;
    }

    @Override
    public String create(CenterDraftModel draft) {
        MsgAnnouncement notice = new MsgAnnouncement();
        notice.setTitle(draft.getTitle());
        notice.setContent(draft.getContent());
        notice.setStatus(AnnouncementStatus.DRAFT.getCode());
        notice.setPublisher(SecurityUtils.getUser().getName());
        Assert.isTrue(msgAnnouncementDao.insert(notice) == 1, "公告保存失败");
        return notice.getId();
    }

    @Override
    public void update(CenterDraftModel draft) {
        // 编辑后保存为草稿，重新发送时生成新的待办，原待办的阅读状态保留。
        Assert.isTrue(msgAnnouncementDao.update(new LambdaUpdateWrapper<MsgAnnouncement>().eq(MsgAnnouncement::getId, draft.getId())
                .set(MsgAnnouncement::getTitle, draft.getTitle()).set(MsgAnnouncement::getContent, draft.getContent())
                .set(MsgAnnouncement::getStatus, AnnouncementStatus.DRAFT.getCode()).set(MsgAnnouncement::getPublishTime, null)
                .set(MsgAnnouncement::getUpdateTime, new Date())) == 1, "公告更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> send(CenterSendModel request) {
        MsgAnnouncement notice = msgAnnouncementDao.selectById(request.getId());
        Assert.notNull(notice, "公告不存在");
        PendingTaskModel model = new PendingTaskModel();
        model.setType(PendingTaskType.ANNOUNCEMENT);
        model.setBusinessId(request.getId());
        model.setTitle(notice.getTitle());
        model.setContent(notice.getContent());
        Assert.isTrue(msgAnnouncementDao.update(new LambdaUpdateWrapper<MsgAnnouncement>().eq(MsgAnnouncement::getId, request.getId())
                .set(MsgAnnouncement::getStatus, AnnouncementStatus.PUBLISHED.getCode())
                .set(MsgAnnouncement::getPublishTime, new Date()).set(MsgAnnouncement::getUpdateTime, new Date())) == 1, "公告发送失败");
        List<String> ids = new ArrayList<>();
        for (String userId : request.getUserIds().stream().map(String::trim).distinct().toList()) {
            model.setUserId(userId);
            ids.add(pendingTaskManager.savePendingTask(model).getId());
        }
        return ids;
    }

    @Override
    public void status(CenterStatusModel request) {
        Assert.isTrue(request.getStatus() == AnnouncementStatus.DRAFT.getCode() || request.getStatus() == AnnouncementStatus.WITHDRAWN.getCode(), "发布公告请使用发送接口");
        Assert.isTrue(msgAnnouncementDao.update(new LambdaUpdateWrapper<MsgAnnouncement>().eq(MsgAnnouncement::getId, request.getId())
                .set(MsgAnnouncement::getStatus, request.getStatus()).set(MsgAnnouncement::getPublishTime, null)
                .set(MsgAnnouncement::getUpdateTime, new Date())) == 1, "公告状态更新失败");
    }

    @Override
    public void delete(String id) {
        Assert.isTrue(msgAnnouncementDao.deleteById(id) == 1, "公告删除失败");
    }
}
