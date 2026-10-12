package cn.poria.message.service.im.tx.util;

import cn.hutool.json.JSONUtil;
import cn.poria.message.enums.im.type.*;

import java.util.HashMap;
import java.util.Map;

/**
 * im 消息转换类
 */
public class TxIMMsgWrapUtil {

    public static Map<String,Object> wrapBody(IMMsgBody content){

        Map<String,Object> body = new HashMap<>();
        if(content == null){
            return body;
        }

        body.put("MsgType",content.getMsgType());
        switch (content.getMsgType()) {
            case "TIMTextElem":
                IMMsgTypeTXT txt = (IMMsgTypeTXT) content;
                body.put("MsgContent",JSONUtil.parseObj(txt).toBean(Object.class));
                break;
            case "TIMCustomElem":
                IMMsgTypeCustom custom = (IMMsgTypeCustom) content;
                body.put("MsgContent",CustomerDataUtil.convertToCommonCustomerMsg(custom.getContent()));
                break;
            case "TIMFileElem":
                IMMsgTypeFile file = (IMMsgTypeFile) content;
                body.put("MsgContent",JSONUtil.parseObj(file).toBean(Object.class));
                break;
            case "TIMImageElem":
                IMMsgTypeIMG img = (IMMsgTypeIMG) content;
                body.put("MsgContent",JSONUtil.parseObj(img).toBean(Object.class));
                break;
            case "TIMLocationElem":
                IMMsgTypeLocation location = (IMMsgTypeLocation) content;
                body.put("MsgContent",JSONUtil.parseObj(location).toBean(Object.class));
                break;
            case "TIMVideoFileElem":
                IMMsgTypeVideo video = (IMMsgTypeVideo) content;
                body.put("MsgContent",JSONUtil.parseObj(video).toBean(Object.class));
                break;
            case "TIMSoundElem":
                IMMsgTypeVoice sound = (IMMsgTypeVoice) content;
                body.put("MsgContent",JSONUtil.parseObj(sound).toBean(Object.class));
                break;
            default:
                break;
        }
        return body;
    }

}
