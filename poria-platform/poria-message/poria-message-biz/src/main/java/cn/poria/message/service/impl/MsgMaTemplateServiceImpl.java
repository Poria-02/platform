package cn.poria.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.message.dao.MsgMaTemplateDao;
import cn.poria.message.entity.MsgMaTemplate;
import cn.poria.message.service.MsgMaTemplateService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * (MsgMaTemplate)表服务实现类
 *
 * @author 张春蕾
 * @since 2024-08-06 19:49:13
 */
@Slf4j
@Service("msgMaTemplateService")
public class MsgMaTemplateServiceImpl extends ServiceImpl<MsgMaTemplateDao, MsgMaTemplate> implements MsgMaTemplateService {

}
