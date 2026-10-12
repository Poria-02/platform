package cn.poria.message.service.informMes;

import cn.poria.message.vo.request.InformMesModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InformPhoneHandler implements InformMessageHandler {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerInformMes(InformMesModel model) {
        log.info("电话。。。");
    }
}
