package cn.poria.message.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.message.vo.request.yxxh.YxxhAXBModel;
import cn.poria.message.vo.request.yxxh.YxxhUnbindModel;
import cn.poria.message.vo.response.YxxhResponseResult;
import cn.poria.common.core.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @param: 云信小号工具
 * @author: wuyifan
 * @date:2024/3/25 16:26
 */
@Slf4j
public class YxxhUtil {


    /**
     * 接口地址 https://101.37.133.245:11008/platform/version/functionCode/appId/sig
     * 传输协议 HTTPS
     * 提交方式 POST
     * 数据格式 JSON
     * 字符编码 UTF-8
     * 加密算法 md5，共 32 位小写，适用于本文档全部接口
     * 参数说明
     * platform 应用平台，语音为 voice
     * version 应用平台版本号，语音版本号为 1.0.0
     * functionCode 功能代码，例：AXB 模式绑定：middleNumberAXB
     * appId 用户账号，请在控制台-应用管理页面获取
     * token 用户账号授权令牌，请在控制台-应用管理页面获取
     * sig 验证信息，sig= md5(appId+token+时间戳) ，不含+号
     * Authorization 包头验证信息，Authorization= Base64(appId:时间戳)，英文冒号
     * 时间戳 格式“unix 时间戳”，精确到毫秒，例：1502261073000，时间戳有效时间为5分钟。
     * 功能参数 各功能对应参数放入 body 中，具体参数参见文档各接口详情
     */
    //云信小号AXB绑定
    private static final String YXXH_AXB = "/voice/1.0.0/middleNumberAXB";
    //云信小号AXB解绑
    private static final String YXXH_UNBIND = "/voice/1.0.0/middleNumberUnbind";

    private static final String YXXH_REAL_NAME = "/voice/1.0.0/realNameAuth/";

    private static String endpoint(String baseUrl, String path) {
        Assert.isTrue(StringUtils.isNotBlank(baseUrl), "云信小号服务地址未配置");
        return StringUtils.stripEnd(baseUrl, "/") + path;
    }

    /**
     * @param: AXB模式绑定
     * @author: wuyifan
     * @date: 2024/3/25 16:33
     */
    public static YxxhResponseResult httpsSendYxxhAXB(YxxhAXBModel body, String appId, String token, String baseUrl) throws Exception {
        long time = System.currentTimeMillis();
        String requestBody = JSONUtil.toJsonStr(body);
        String path = getUrl(endpoint(baseUrl, YXXH_AXB), appId, token, time);
        String headerStr = getHeader(appId, time);

        String result = doPost(path, headerStr, requestBody);
        YxxhResponseResult notifyResp = new YxxhResponseResult();
        if (result != null) {
            notifyResp = JSONUtil.toBean(result, YxxhResponseResult.class);
        }

        return notifyResp;
    }

    public static YxxhResponseResult httpsSendYxxhUnbind(YxxhUnbindModel body, String appId, String token, String baseUrl) throws Exception {
        long time = System.currentTimeMillis();
        String headerStr = getHeader(appId, time);
        String requestBody = JSONUtil.toJsonStr(body);
        String url = getUrl(endpoint(baseUrl, YXXH_UNBIND), appId, token, time);
        String result = doPost(url, headerStr, requestBody);
        YxxhResponseResult notifyResp = new YxxhResponseResult();
        if (StringUtils.isNotBlank(result)) {
            notifyResp = JSONUtil.toBean(result, YxxhResponseResult.class);
        }

        return notifyResp;
    }

    /**
     * 实名认证
     * @param param
     * @param appId
     * @param token
     */
    public static JSONObject realNameAuth(JSONObject param, String appId, String token, String baseUrl) throws Exception {

        long time = System.currentTimeMillis();
        String headerStr = getHeader(appId, time);
        String requestBody = JSONUtil.toJsonStr(param);
        String url = getUrl(endpoint(baseUrl, YXXH_REAL_NAME), appId, token, time);
        String result = doPost(url, headerStr, requestBody);
        log.info("实名认证返回信息:{}", result);
        return JSONUtil.parseObj(result);

    }

    private static String doPost(String url, String header, String body) throws Exception {
        HttpRequest post = HttpUtil.createPost(url);
        post.header("Content-Type", "application/json; charset=utf-8");
        post.header("Authorization", header);
        post.body(body);
        HttpResponse response = post.execute();
        return response != null && response.body() != null ? response.body() : null;
    }

    private static String getUrl(String urlName, String accountId, String token, long time) throws Exception {
        String url = urlName;
        String sig = SecureUtil.md5(accountId + token + time);
        url = url + "/" + accountId + "/" + sig;
        return url;
    }

    private static String getHeader(String accountId, Long time) {
        String Authorization = accountId + ":" + time;
        return Base64.encode(Authorization);
    }
}
