package cn.poria.message.service.impl;

import cn.hutool.core.util.StrUtil;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String TEMPLATE_KEY = "MESSAGE_TEMPLATE_";

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
                wapper->wapper.like(MsgTemplate::getTitle,queryModel.getKeywords()).or().like(MsgTemplate::getContent,queryModel.getKeywords()));
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
     * 根据编码查询模版
     * @param key
     * @return
     */
    @Override
    public TemplateVo selectByKey(String key) {

        TemplateVo templateVo = (TemplateVo) redisTemplate.opsForValue().get(TEMPLATE_KEY + key);
        if(templateVo == null){
            MsgTemplate template = this.getOne(new QueryWrapper<MsgTemplate>().eq("template_key",key));
            if(template != null){
                templateVo = new TemplateVo();
                BeanUtils.copyProperties(template,templateVo);
                redisTemplate.opsForValue().set(TEMPLATE_KEY + key,templateVo);
            }
        }
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
        redisTemplate.delete(TEMPLATE_KEY + localTemplate.getTemplateKey());
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

        TemplateVo template = selectByKey(key);

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

        MsgTemplate template = this.getById(id);
        this.removeById(id);
        redisTemplate.delete(TEMPLATE_KEY + template.getTemplateKey());

    }

    /**
     * 更新状态
     *
     * @param id
     * @param status
     */
    @Override
    public void changeStatus(String id, Integer status) {
        MsgTemplate template = this.getById(id);
        Assert.notNull(template,"模版不存在");
        redisTemplate.delete(TEMPLATE_KEY + template.getTemplateKey());
        template.setStatus(status);
        this.updateById(template);
    }
}
