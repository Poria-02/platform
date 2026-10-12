package cn.poria.message.service;

import cn.poria.message.entity.MsgTemplate;
import cn.poria.message.vo.request.template.TemplateModel;
import cn.poria.message.vo.request.yxxh.YxxhAXBModel;
import cn.poria.message.vo.request.yxxh.YxxhPushModel;
import cn.poria.message.vo.request.yxxh.YxxhRealNameModel;
import cn.poria.message.vo.request.yxxh.YxxhUnbindModel;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.poria.message.entity.MsgYxxhInfo;

/**
 * (MsgYxxhInfo)表服务接口
 *
 * @author wuyifan
 * @since 2024-03-25 16:15:24
 */
public interface MsgYxxhInfoService extends IService<MsgYxxhInfo> {

    String yxxhAXB(YxxhAXBModel model);
    void yxxhUnbind(YxxhUnbindModel model);
    void yxxhPush(YxxhPushModel model);

    /**
     * 隐私通话实名认证
     * @param yxxhRealNameModel
     */
    String realName(YxxhRealNameModel yxxhRealNameModel);
}

