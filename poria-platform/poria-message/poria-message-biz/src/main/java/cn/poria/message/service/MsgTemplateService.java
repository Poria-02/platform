package cn.poria.message.service;

import cn.poria.message.entity.MsgTemplate;
import cn.poria.message.vo.request.ma.MaTemplateQueryModel;
import cn.poria.message.vo.request.template.TemplateModel;
import cn.poria.message.vo.request.template.TemplateQueryModel;
import cn.poria.message.vo.response.template.TemplateVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.Map;

/**
 * (MsgTemplate)表服务接口
 *
 * @author makejava
 * @since 2020-05-20 15:03:12
 */
public interface MsgTemplateService extends IService<MsgTemplate> {


    IPage<TemplateVo> page(TemplateQueryModel queryModel);
    /**
     * 新增模板
     * @param model
     */
    MsgTemplate addTemplate(TemplateModel model);



    /**
     * 根据编码查询模版
     * @param code
     * @return
     */
    TemplateVo selectByKey(String code);

    /**
     * 查询指定微信小程序已启用的订阅消息模板ID
     *
     * @param queryModel 微信小程序AppId及订阅消息模板Key列表
     * @return 模板Key与微信小程序订阅消息模板ID的对应关系
     */
    Map<String, String> getMaTemplateIds(MaTemplateQueryModel queryModel);

    /**
     * 修改模板
     * @param model
     */
    MsgTemplate updateTemplate(TemplateModel model);

    /**
     * 根据模版编码，返回替换后的内容
     * @param key
     * @return
     */
    String getContentByKey(String key, Map<String,String> param);

    /**
     * 删除
     * @param id
     */
    void deleteTemplate(Serializable id);

    /**
     * 更新状态
     * @param id
     * @param status
     */
    void changeStatus(String id, Integer status);
}
