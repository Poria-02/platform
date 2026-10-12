package cn.poria.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.poria.message.dao.MsgImTemplateDao;
import cn.poria.message.entity.MsgImTemplate;
import cn.poria.message.service.MsgImTemplateService;
import cn.poria.message.vo.request.im.IMTemplateQueryModel;
import cn.poria.message.vo.response.im.IMTemplateVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * IM消息模板(MsgImTemplate)表服务实现类
 *
 * @author wuning
 * @since 2025-05-07 10:47:07
 */
@Slf4j
@Service("msgImTemplateService")
public class MsgImTemplateServiceImpl extends ServiceImpl<MsgImTemplateDao, MsgImTemplate> implements MsgImTemplateService {

    @Override
    public IPage<IMTemplateVo> queryPage(IMTemplateQueryModel queryModel) {
        Page page = new Page<>(queryModel.getCurrent(), queryModel.getSize());
        IPage<IMTemplateVo> iPage = page(page, new LambdaQueryWrapper<MsgImTemplate>()
                .eq(StringUtils.isNotBlank(queryModel.getAccountId()), MsgImTemplate::getAccountId, queryModel.getAccountId())
                .like(StringUtils.isNotBlank(queryModel.getContent()), MsgImTemplate::getContent, queryModel.getContent())).convert(msgImTemplate -> BeanUtil.copyProperties(msgImTemplate, IMTemplateVo.class));
        return iPage;
    }
}
