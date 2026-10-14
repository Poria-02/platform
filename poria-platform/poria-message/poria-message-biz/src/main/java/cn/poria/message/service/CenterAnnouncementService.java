package cn.poria.message.service;

import cn.poria.message.vo.request.center.CenterDraftModel;
import cn.poria.message.vo.request.center.CenterQueryModel;
import cn.poria.message.vo.request.center.CenterSendModel;
import cn.poria.message.vo.response.center.AnnouncementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 管理平台公示公告服务接口
 */
public interface CenterAnnouncementService {

    /**
     * 分页查询公示公告记录
     *
     * @param query 查询条件
     * @return 公示公告记录分页数据
     */
    IPage<AnnouncementVo> page(CenterQueryModel query);

    /**
     * 查询公示公告记录详情
     *
     * @param id 公告ID
     * @return 公示公告详情
     */
    AnnouncementVo detail(String id);

    /**
     * 新增公示公告草稿
     *
     * @param draft 公告内容
     * @return 公告ID
     */
    String create(CenterDraftModel draft);

    /**
     * 修改公示公告内容
     *
     * @param id 公告ID
     * @param draft 公告内容
     */
    void update(String id, CenterDraftModel draft);

    /**
     * 通过待办功能发送公示公告
     *
     * @param id 公告ID
     * @param model 接收人用户ID列表
     * @return 待办任务ID列表
     */
    List<String> send(String id, CenterSendModel model);

    /**
     * 恢复草稿或撤回公示公告
     *
     * @param id 公告ID
     * @param status 0-草稿，2-已撤回
     */
    void status(String id, int status);

    /**
     * 删除公示公告记录
     *
     * @param id 公告ID
     */
    void delete(String id);
}
