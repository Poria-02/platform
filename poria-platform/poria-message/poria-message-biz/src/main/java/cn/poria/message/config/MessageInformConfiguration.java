package cn.poria.message.config;

import cn.poria.message.constant.InformMessageConstant;
import cn.poria.message.service.informMes.InformMessageHandler;
import cn.poria.message.service.informMes.InformSmsHandler;
import cn.poria.message.service.informMes.InformWxHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
@Slf4j
public class MessageInformConfiguration {

    private final InformWxHandler wxHandler;
    private final InformSmsHandler smsHandler;

    //	private static final Map<Integer, ManageTaskHandler>  manageTaskHandlerMap = Maps.newHashMap();
    private static final Map<Integer, InformMessageHandler> HealthInformHandlerMap = new HashMap<>();


    @Autowired
    public MessageInformConfiguration(InformWxHandler wxHandler, InformSmsHandler smsHandler) {
        this.wxHandler = wxHandler;
        this.smsHandler = smsHandler;
    }

    @PostConstruct
    public void init(){
        HealthInformHandlerMap.put(InformMessageConstant.INFORM_WX, wxHandler);
        HealthInformHandlerMap.put(InformMessageConstant.INFORM_SMS,smsHandler);
        log.info("用户任务处理类初始化成功: {}",HealthInformHandlerMap);
    }


    public static InformMessageHandler get(Integer type){
        return HealthInformHandlerMap.get(type);
    }

}
