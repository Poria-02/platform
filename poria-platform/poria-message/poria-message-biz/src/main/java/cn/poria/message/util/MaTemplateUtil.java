package cn.poria.message.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * 小程序订阅消息模板配置解析工具
 */
public final class MaTemplateUtil {

    private MaTemplateUtil() {
    }

    /**
     * 将业务参数映射为小程序订阅模板字段
     *
     * @param config 平台字段与业务参数的映射配置
     * @param param 已填充公共参数的业务参数
     * @return 实际发送给小程序平台的字段值
     */
    public static Map<String, String> matchParam(JSONArray config, Map<String, String> param) {
        Assert.isTrue(CollectionUtil.isNotEmpty(config), "小程序模板参数映射未配置");
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < config.size(); i++) {
            JSONObject json = config.getJSONObject(i);
            Assert.notNull(json, "小程序模板参数映射项不能为空");
            String key = json.getStr("key");
            Assert.isTrue(StrUtil.isNotBlank(key), "小程序模板字段名不能为空");
            String valueKey = StrUtil.blankToDefault(json.getStr("valueKey"), key);
            Assert.notNull(param.get(valueKey), "小程序模板参数[{}]未提供", valueKey);
            data.put(key, param.get(valueKey));
        }
        return data;
    }

    /**
     * 按小程序AppId精确获取模板配置
     *
     * @param content 模板配置JSON数组
     * @param appId 小程序AppId
     * @return 对应的小程序配置，未配置时返回null
     */
    public static JSONObject getConfig(String content, String appId) {
        if (StrUtil.isBlank(appId)) {
            return null;
        }
        if (!JSONUtil.isTypeJSONArray(content)) {
            throw new ServiceException("小程序模板内容须为JSON数组");
        }

        JSONArray configs;
        try {
            configs = JSONUtil.parseArray(content);
        } catch (Exception e) {
            throw new ServiceException("小程序模板配置JSON格式错误");
        }

        for (Object item : configs) {
            if (!(item instanceof JSONObject config)) {
                throw new ServiceException("小程序模板配置项须为JSON对象");
            }
            if (StrUtil.equals(config.getStr("appId"), appId)) {
                return config;
            }
        }
        return null;
    }
}
