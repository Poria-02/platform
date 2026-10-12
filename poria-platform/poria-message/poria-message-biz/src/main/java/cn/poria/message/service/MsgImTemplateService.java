package cn.poria.message.service;

import cn.poria.message.entity.MsgImTemplate;
import cn.poria.message.vo.request.im.IMTemplateQueryModel;
import cn.poria.message.vo.response.im.IMTemplateVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * IM消息模板(MsgImTemplate)表服务接口
 *
 * @author wuning
 * @since 2025-05-07 10:47:07
 */
public interface MsgImTemplateService extends IService<MsgImTemplate> {

    IPage<IMTemplateVo> queryPage(IMTemplateQueryModel queryModel);
}
