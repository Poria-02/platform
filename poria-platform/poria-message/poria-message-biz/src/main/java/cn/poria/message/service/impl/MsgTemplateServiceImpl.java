package cn.poria.message.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.poria.common.core.constant.CacheConstants;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.TemplateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.message.dao.MsgTemplateDao;
import cn.poria.message.entity.MsgTemplate;
import cn.poria.message.service.MsgTemplateService;
import cn.poria.message.vo.request.template.TemplateModel;
import cn.poria.message.vo.request.template.TemplateQueryModel;
import cn.poria.message.vo.response.template.TemplateVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Map;

/**
 * (MsgTemplate)表服务实现类
 *
 * @author makejava
 * @since 2020-05-20 15:03:12
 */
@Service
public class MsgTemplateServiceImpl extends ServiceImpl<MsgTemplateDao, MsgTemplate> implements MsgTemplateService {

    /**
     * 模板服务代理，内部查询通过代理调用以使缓存注解生效
     */
    @Lazy
    @Resource
    private MsgTemplateService msgTemplateService;

    @Override
    public IPage<TemplateVo> page(TemplateQueryModel queryModel) {
        Page page = new Page(queryModel.getCurrent(),queryModel.getSize());
        page.addOrder(OrderItem.desc("create_time"));

        LambdaQueryWrapper<MsgTemplate> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(StrUtil.isNotBlank(queryModel.getStatus()),MsgTemplate::getStatus,queryModel.getStatus());
        queryWrapper.eq(StrUtil.isNotBlank(queryModel.getType()),MsgTemplate::getType,queryModel.getType());
        queryWrapper.eq(StrUtil.isNotBlank(queryModel.getCategory()),MsgTemplate::getCategory,queryModel.getCategory());
        queryWrapper.eq(StrUtil.isNotBlank(queryModel.getTemplateKey()),MsgTemplate::getTemplateKey,queryModel.getTemplateKey());
        queryWrapper.and(StrUtil.isNotBlank(queryModel.getKeywords()),
                wapper->wapper.like(MsgTemplate::getTitle,queryModel.getKeywords()).or().like(MsgTemplate::getContent,queryModel.getKeywords())
                        .or().like(MsgTemplate::getTemplateKey,queryModel.getKeywords()));
        IPage<MsgTemplate> result = this.page(page,queryWrapper);
        return  result.convert(msgTemplate -> {
            TemplateVo templateVo = new TemplateVo();
            BeanUtils.copyProperties(msgTemplate,templateVo);
            return templateVo;
        });
    }

    /**
     * 新增模板
     *
     * @param model
     */
    @Override
    @Transactional
    public MsgTemplate addTemplate(TemplateModel model) {
        MsgTemplate local= this.getOne(new QueryWrapper<MsgTemplate>().eq("template_key",model.getTemplateKey()));
        Assert.isNull(local,"模版key:{}已存在",model.getTemplateKey());

        MsgTemplate msgTemplate = new MsgTemplate();
        BeanUtils.copyProperties(model,msgTemplate);
        this.save(msgTemplate);

        return msgTemplate;
    }

    /**
     * 根据模板Key查询消息模板，不缓存不存在的模板
     *
     * @param key 模板Key
     * @return 消息模板详情，不存在时返回null
     */
    @Override
    @Cacheable(value = CacheConstants.MESSAGE_TEMPLATE, key = "#key", unless = "#result == null")
    public TemplateVo selectByKey(String key) {

        MsgTemplate template = this.getOne(new LambdaQueryWrapper<MsgTemplate>()
                .eq(MsgTemplate::getTemplateKey, key));
        if (template == null) {
            return null;
        }

        TemplateVo templateVo = new TemplateVo();
        BeanUtils.copyProperties(template, templateVo);
        return templateVo;
    }


    /**
     * 修改模板
     * @param model
     */
    @Override
    @Transactional
    public MsgTemplate updateTemplate(TemplateModel model) {

        //查询本地数据先
        MsgTemplate localTemplate = this.getById(model.getId());
        Assert.notNull(localTemplate,"模板不存在");
        //更新模板
        BeanUtils.copyProperties(model,localTemplate);
        this.updateById(localTemplate);

        return localTemplate;
    }

    /**
     * 根据模版编码，返回替换后的内容
     *
     * @param key
     * @param param
     * @return
     */
    @Override
    public String getContentByKey(String key, Map<String, String> param) {

        TemplateVo template = msgTemplateService.selectByKey(key);

        Assert.notNull(template,"模版:{}编码不存在",key);

        return TemplateUtil.process(template.getContent(),param);
    }



    /**
     * 删除
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteTemplate(Serializable id) {

        this.removeById(id);

    }

    /**
     * 更新状态
     *
     * @param id
     * @param status
     */
    @Override
    @Transactional
    public void changeStatus(String id, Integer status) {
        MsgTemplate template = this.getById(id);
        Assert.notNull(template,"模版不存在");
        template.setStatus(status);
        this.updateById(template);
    }
}
