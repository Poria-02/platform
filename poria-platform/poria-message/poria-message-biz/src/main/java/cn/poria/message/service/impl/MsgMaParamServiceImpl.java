package cn.poria.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.message.dao.MsgMaParamDao;
import cn.poria.message.entity.MsgMaParam;
import cn.poria.message.service.MsgMaParamService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * (MsgMaParam)表服务实现类
 *
 * @author 张春蕾
 * @since 2024-08-06 19:48:46
 */
@Slf4j
@Service("msgMaParamService")
public class MsgMaParamServiceImpl extends ServiceImpl<MsgMaParamDao, MsgMaParam> implements MsgMaParamService {

}
