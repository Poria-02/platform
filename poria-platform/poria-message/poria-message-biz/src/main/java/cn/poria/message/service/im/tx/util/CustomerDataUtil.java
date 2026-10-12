package cn.poria.message.service.im.tx.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.*;

public class CustomerDataUtil {

    /**
     * 转换map为tx customer
     */
    public static List<Map<String,Object>> convertToTxCustomerData(Map<String,Object> customerDataMap){
        List<Map<String,Object>> vos = new LinkedList<>();
        Object serviceProcess = customerDataMap.get("serviceProcess");
        if(serviceProcess != null){
            vos.add(convertKeyValueMap("serviceProcess",String.valueOf(serviceProcess)));
        }

        JSONObject payLoad = JSONUtil.parseObj(customerDataMap);
        payLoad.remove("serviceProcess");
        vos.add(convertKeyValueMap("payload",payLoad));
        return vos;
    }

    /**
     * 转换tx customer为 map
     */
    public static String convertToCommonCustomerData(List<Map<String,String>> txCustomerData){
        Map<String,Object> vo = new HashMap<>();
        for (Map<String, String> customerBean : txCustomerData) {
            String key = customerBean.get("Key");
            String value = customerBean.get("Value");
            if(key.equals("serviceProcess")){
                vo.put(key,value);
            }

            if(key.equals("payload")){
                vo.put("payload",value);
            }
        }
        return JSONUtil.toJsonStr(vo);
    }


    /**
     * 转换自定义消息
     */
    public static Map<String,Object> convertToCommonCustomerMsg(Map<String,Object> bodyMap){
        JSONObject body = JSONUtil.parseObj(bodyMap);
        Map<String,Object> customMsgBody = new HashMap<>();

        Map<String,Object> customMsgBodyData = new HashMap<>();

        if(StrUtil.isNotBlank(body.getStr("type"))){
            customMsgBodyData.put("type",body.getStr("type"));
        }

        customMsgBody.put("Desc",String.valueOf(body.get("desc")));
        customMsgBody.put("Ext",String.valueOf(body.get("ext")));
        customMsgBody.put("Sound",String.valueOf(body.get("sound")));

        body.remove("type");
        body.remove("desc");
        body.remove("ext");
        body.remove("sound");
        customMsgBodyData.put("data",body.toBean(Object.class));
        customMsgBody.put("Data",JSONUtil.toJsonStr(customMsgBodyData));

        return customMsgBody;
    }


    private static Map<String,Object> convertKeyValueMap(String key,Object value){
        Map<String,Object> vo = new LinkedHashMap<>();
        vo.put("Key",key);
        vo.put("Value",String.valueOf(value));
        return vo;
    }

}
